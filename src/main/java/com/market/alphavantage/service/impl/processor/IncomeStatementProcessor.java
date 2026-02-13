package com.market.alphavantage.service.impl.processor;

import com.market.alphavantage.dto.CashFlowDTO;
import com.market.alphavantage.dto.IncomeStatementDTO;
import com.market.alphavantage.entity.IncomeStatement;
import com.market.alphavantage.repository.IncomeStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IncomeStatementProcessor {

    private final IncomeStatementRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    /**
     * Fetch, parse, save and return DTO
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSymbol(String symbol) {
        log("Fetching IncomeStatement for symbol: " + symbol);

        Map<String, Object> response = fetchFromApi(symbol);
        if (response == null || !response.containsKey("annualReports")) {
            log("No data returned for symbol: " + symbol);
        }

        List<Map<String, String>> annualReports = (List<Map<String, String>>) response.get("annualReports");
        List<Map<String, String>> quarterlyReports = (List<Map<String, String>>) response.get("quarterlyReports");

        if (annualReports != null) Collections.reverse(annualReports);
        if (quarterlyReports != null) Collections.reverse(quarterlyReports);

        IncomeStatement entity = repository.findById(symbol).orElseGet(IncomeStatement::new);
        entity.setSymbol(symbol);

        // ===== ANNUAL =====
        entity.setAnnualFiscalDateEnding(parseDateArray(annualReports, "fiscalDateEnding"));
        entity.setAnnualTotalRevenue(parseLongArray(annualReports, "totalRevenue"));
        entity.setAnnualCostOfRevenue(parseLongArray(annualReports, "costOfRevenue"));
        entity.setAnnualGrossProfit(parseLongArray(annualReports, "grossProfit"));
        entity.setAnnualOperatingExpenses(parseLongArray(annualReports, "operatingExpenses"));
        entity.setAnnualOperatingIncome(parseLongArray(annualReports, "operatingIncome"));
        entity.setAnnualEbit(parseLongArray(annualReports, "ebit"));
        entity.setAnnualEbitda(parseLongArray(annualReports, "ebitda"));
        entity.setAnnualInterestExpense(parseLongArray(annualReports, "interestExpense"));
        entity.setAnnualIncomeBeforeTax(parseLongArray(annualReports, "incomeBeforeTax"));
        entity.setAnnualIncomeTaxExpense(parseLongArray(annualReports, "incomeTaxExpense"));
        entity.setAnnualNetIncome(parseLongArray(annualReports, "netIncome"));
        entity.setAnnualNetIncomeFromContinuingOperations(parseLongArray(annualReports, "netIncomeFromContinuingOperations"));

        // ===== QUARTERLY =====
        entity.setQuarterlyFiscalDateEnding(parseDateArray(quarterlyReports, "fiscalDateEnding"));
        entity.setQuarterlyTotalRevenue(parseLongArray(quarterlyReports, "totalRevenue"));
        entity.setQuarterlyCostOfRevenue(parseLongArray(quarterlyReports, "costOfRevenue"));
        entity.setQuarterlyGrossProfit(parseLongArray(quarterlyReports, "grossProfit"));
        entity.setQuarterlyOperatingExpenses(parseLongArray(quarterlyReports, "operatingExpenses"));
        entity.setQuarterlyOperatingIncome(parseLongArray(quarterlyReports, "operatingIncome"));
        entity.setQuarterlyEbit(parseLongArray(quarterlyReports, "ebit"));
        entity.setQuarterlyEbitda(parseLongArray(quarterlyReports, "ebitda"));
        entity.setQuarterlyInterestExpense(parseLongArray(quarterlyReports, "interestExpense"));
        entity.setQuarterlyIncomeBeforeTax(parseLongArray(quarterlyReports, "incomeBeforeTax"));
        entity.setQuarterlyIncomeTaxExpense(parseLongArray(quarterlyReports, "incomeTaxExpense"));
        entity.setQuarterlyNetIncome(parseLongArray(quarterlyReports, "netIncome"));
        entity.setQuarterlyNetIncomeFromContinuingOperations(parseLongArray(quarterlyReports, "netIncomeFromContinuingOperations"));

        repository.save(entity);
        log("Saved IncomeStatement entity for symbol: " + symbol);

    }

    /* ================= GET DTO ================= */
    public IncomeStatementDTO getIncFlow(String symbol) {
        return repository.findById(symbol)
                .map(e -> new IncomeStatementDTO(e))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchFromApi(String symbol) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            String url = String.format("%s?function=INCOME_STATEMENT&symbol=%s&apikey=%s", baseUrl, symbol, apiKey);
            log("Calling API for symbol: " + symbol);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log("API call completed for symbol: " + symbol);
            return response;
        } catch (Exception e) {
            logErr("Failed API call for symbol: " + symbol + " -> " + e.getMessage());
            return null;
        }
    }

    /* =================== HELPERS =================== */

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("None")) return null;
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate[] parseDateArray(List<Map<String, String>> reports, String key) {
        if (reports == null || reports.isEmpty()) return new LocalDate[0];
        return reports.stream().map(r -> parseDate(r.get(key))).toArray(LocalDate[]::new);
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("None")) return 0L;
        try {
            return Double.valueOf(value).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    private Long[] parseLongArray(List<Map<String, String>> reports, String key) {
        if (reports == null || reports.isEmpty()) return new Long[0];
        return reports.stream().map(r -> parseLong(r.get(key))).toArray(Long[]::new);
    }

    private void log(String message) {
        System.out.println("[" + LocalDateTime.now().format(dtf) + "] INFO: " + message);
    }

    private void logErr(String message) {
        System.err.println("[" + LocalDateTime.now().format(dtf) + "] ERROR: " + message);
    }
}
