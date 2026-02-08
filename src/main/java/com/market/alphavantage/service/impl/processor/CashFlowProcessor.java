package com.market.alphavantage.service.impl.processor;

import com.market.alphavantage.dto.CashFlowDTO;
import com.market.alphavantage.entity.CashFlow;
import com.market.alphavantage.repository.CashFlowRepository;
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

@Service
@RequiredArgsConstructor
public class CashFlowProcessor {

    private final CashFlowRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /* ================= PROCESS SYMBOL ================= */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSymbol(String symbol) {

        String url = String.format("%s?function=CASH_FLOW&symbol=%s&apikey=%s", baseUrl, symbol, apiKey);
        Map<String, Object> response;

        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (Exception ex) {
            logErr("API call failed for symbol " + symbol + ": " + ex.getMessage());
            return;
        }

        if (response == null || !response.containsKey("annualReports")) {
            log("No data returned for " + symbol);
            return;
        }

        List<Map<String, String>> annualReports = (List<Map<String, String>>) response.get("annualReports");
        List<Map<String, String>> quarterlyReports = (List<Map<String, String>>) response.get("quarterlyReports");

        if (annualReports != null) Collections.reverse(annualReports);   // oldest → newest
        if (quarterlyReports != null) Collections.reverse(quarterlyReports);

        CashFlow entity = repository.findById(symbol).orElseGet(CashFlow::new);
        entity.setSymbol(symbol);

        /* ===== Annual ===== */
        entity.setAnnualFiscalDateEnding(parseDateArray(annualReports, "fiscalDateEnding"));
        entity.setAnnualOperatingCashflow(parseLongArray(annualReports, "operatingCashflow"));
        entity.setAnnualPaymentsForOperatingActivities(parseLongArray(annualReports, "paymentsForOperatingActivities"));
        entity.setAnnualProceedsFromOperatingActivities(parseLongArray(annualReports, "proceedsFromOperatingActivities"));
        entity.setAnnualChangeInCash(parseLongArray(annualReports, "changeInCash"));
        entity.setAnnualCashflowFromInvestment(parseLongArray(annualReports, "cashflowFromInvestment"));
        entity.setAnnualCashflowFromFinancing(parseLongArray(annualReports, "cashflowFromFinancing"));
        entity.setAnnualProceedsFromRepaymentsOfShortTermDebt(parseLongArray(annualReports, "proceedsFromRepaymentsOfShortTermDebt"));
        entity.setAnnualPaymentsForRepurchaseOfCommonStock(parseLongArray(annualReports, "paymentsForRepurchaseOfCommonStock"));
        entity.setAnnualPaymentsForRepurchaseOfEquity(parseLongArray(annualReports, "paymentsForRepurchaseOfEquity"));
        entity.setAnnualPaymentsForRepurchaseOfPreferredStock(parseLongArray(annualReports, "paymentsForRepurchaseOfPreferredStock"));
        entity.setAnnualDividendsPaid(parseLongArray(annualReports, "dividendsPaid"));
        entity.setAnnualDividendsPaidOnCommonStock(parseLongArray(annualReports, "dividendsPaidOnCommonStock"));
        entity.setAnnualDividendsPaidOnPreferredStock(parseLongArray(annualReports, "dividendsPaidOnPreferredStock"));
        entity.setAnnualProceedsFromIssuanceOfCommonStock(parseLongArray(annualReports, "proceedsFromIssuanceOfCommonStock"));
        entity.setAnnualProceedsFromIssuanceOfLongTermDebt(parseLongArray(annualReports, "proceedsFromIssuanceOfLongTermDebt"));
        entity.setAnnualProceedsFromIssuanceOfPreferredStock(parseLongArray(annualReports, "proceedsFromIssuanceOfPreferredStock"));
        entity.setAnnualProceedsFromRepurchaseOfEquity(parseLongArray(annualReports, "proceedsFromRepurchaseOfEquity"));
        entity.setAnnualOtherCashflowFromFinancingActivities(parseLongArray(annualReports, "otherCashflowFromFinancingActivities"));
        entity.setAnnualNetBorrowings(parseLongArray(annualReports, "netBorrowings"));

        /* ===== Quarterly ===== */
        entity.setQuarterlyFiscalDateEnding(parseDateArray(quarterlyReports, "fiscalDateEnding"));
        entity.setQuarterlyOperatingCashflow(parseLongArray(quarterlyReports, "operatingCashflow"));
        entity.setQuarterlyPaymentsForOperatingActivities(parseLongArray(quarterlyReports, "paymentsForOperatingActivities"));
        entity.setQuarterlyProceedsFromOperatingActivities(parseLongArray(quarterlyReports, "proceedsFromOperatingActivities"));
        entity.setQuarterlyChangeInCash(parseLongArray(quarterlyReports, "changeInCash"));
        entity.setQuarterlyCashflowFromInvestment(parseLongArray(quarterlyReports, "cashflowFromInvestment"));
        entity.setQuarterlyCashflowFromFinancing(parseLongArray(quarterlyReports, "cashflowFromFinancing"));
        entity.setQuarterlyProceedsFromRepaymentsOfShortTermDebt(parseLongArray(quarterlyReports, "proceedsFromRepaymentsOfShortTermDebt"));
        entity.setQuarterlyPaymentsForRepurchaseOfCommonStock(parseLongArray(quarterlyReports, "paymentsForRepurchaseOfCommonStock"));
        entity.setQuarterlyPaymentsForRepurchaseOfEquity(parseLongArray(quarterlyReports, "paymentsForRepurchaseOfEquity"));
        entity.setQuarterlyPaymentsForRepurchaseOfPreferredStock(parseLongArray(quarterlyReports, "paymentsForRepurchaseOfPreferredStock"));
        entity.setQuarterlyDividendsPaid(parseLongArray(quarterlyReports, "dividendsPaid"));
        entity.setQuarterlyDividendsPaidOnCommonStock(parseLongArray(quarterlyReports, "dividendsPaidOnCommonStock"));
        entity.setQuarterlyDividendsPaidOnPreferredStock(parseLongArray(quarterlyReports, "dividendsPaidOnPreferredStock"));
        entity.setQuarterlyProceedsFromIssuanceOfCommonStock(parseLongArray(quarterlyReports, "proceedsFromIssuanceOfCommonStock"));
        entity.setQuarterlyProceedsFromIssuanceOfLongTermDebt(parseLongArray(quarterlyReports, "proceedsFromIssuanceOfLongTermDebt"));
        entity.setQuarterlyProceedsFromIssuanceOfPreferredStock(parseLongArray(quarterlyReports, "proceedsFromIssuanceOfPreferredStock"));
        entity.setQuarterlyProceedsFromRepurchaseOfEquity(parseLongArray(quarterlyReports, "proceedsFromRepurchaseOfEquity"));
        entity.setQuarterlyOtherCashflowFromFinancingActivities(parseLongArray(quarterlyReports, "otherCashflowFromFinancingActivities"));
        entity.setQuarterlyNetBorrowings(parseLongArray(quarterlyReports, "netBorrowings"));

        repository.saveAndFlush(entity);
        log("Saved CashFlow for symbol: " + symbol);
    }

    /* ================= GET DTO ================= */
    public CashFlowDTO getCashFlow(String symbol) {
        return repository.findById(symbol)
                .map(e -> new CashFlowDTO(e))
                .orElse(null);
    }

    /* ================= HELPERS ================= */
    private Long parseLong(String val) {
        if (val == null || val.isBlank() || "None".equalsIgnoreCase(val)) return 0L;
        try {
            return Double.valueOf(val).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.isBlank()) return null;
        try {
            return LocalDate.parse(val);
        } catch (Exception e) {
            return null;
        }
    }

    private Long[] parseLongArray(List<Map<String, String>> reports, String key) {
        if (reports == null || reports.isEmpty()) return new Long[0];
        return reports.stream().map(r -> parseLong(r.get(key))).toArray(Long[]::new);
    }

    private LocalDate[] parseDateArray(List<Map<String, String>> reports, String key) {
        if (reports == null || reports.isEmpty()) return new LocalDate[0];
        return reports.stream().map(r -> parseDate(r.get(key))).toArray(LocalDate[]::new);
    }

    private void log(String msg) {
        System.out.println(LocalDateTime.now().format(dtf) + " | " + msg);
    }

    private void logErr(String msg) {
        System.err.println(LocalDateTime.now().format(dtf) + " | " + msg);
    }
}
