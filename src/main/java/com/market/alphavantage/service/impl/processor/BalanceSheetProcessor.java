package com.market.alphavantage.service.impl.processor;

import com.market.alphavantage.entity.BalanceSheet;
import com.market.alphavantage.repository.BalanceSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BalanceSheetProcessor {

    private final BalanceSheetRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSymbol(String symbol) {
        try {
            String url = String.format("%s?function=BALANCE_SHEET&symbol=%s&apikey=%s", baseUrl, symbol, apiKey);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.containsKey("Note") || !response.containsKey("annualReports")) {
                logInfo("No data returned for " + symbol);
                return;
            }

            List<Map<String, String>> annualReports = (List<Map<String, String>>) response.get("annualReports");
            List<Map<String, String>> quarterlyReports = (List<Map<String, String>>) response.get("quarterlyReports");

            annualReports = sortReportsByDateAsc(annualReports);
            quarterlyReports = sortReportsByDateAsc(quarterlyReports);

            BalanceSheet entity = repository.findById(symbol).orElse(new BalanceSheet());
            entity.setSymbol(symbol);

            // Annual
            entity.setAnnualFiscalDateEnding(parseDateListToArray(annualReports, "fiscalDateEnding"));
            entity.setAnnualTotalAssets(parseLongListToArray(annualReports, "totalAssets"));
            entity.setAnnualTotalLiabilities(parseLongListToArray(annualReports, "totalLiabilities"));
            entity.setAnnualTotalShareholderEquity(parseLongListToArray(annualReports, "totalShareholderEquity"));
            entity.setAnnualCashAndCashEquivalents(parseLongListToArray(annualReports, "cashAndCashEquivalentsAtCarryingValue"));
            entity.setAnnualShortTermInvestments(parseLongListToArray(annualReports, "shortTermInvestments"));
            entity.setAnnualNetReceivables(parseLongListToArray(annualReports, "currentNetReceivables"));
            entity.setAnnualInventory(parseLongListToArray(annualReports, "inventory"));
            entity.setAnnualOtherCurrentAssets(parseLongListToArray(annualReports, "otherCurrentAssets"));
            entity.setAnnualOtherAssets(parseLongListToArray(annualReports, "otherNonCurrentAssets"));
            entity.setAnnualAccountsPayable(parseLongListToArray(annualReports, "currentAccountsPayable"));
            entity.setAnnualCurrentDebt(parseLongListToArray(annualReports, "currentDebt"));
            entity.setAnnualLongTermDebt(parseLongListToArray(annualReports, "longTermDebt"));
            entity.setAnnualOtherCurrentLiabilities(parseLongListToArray(annualReports, "otherCurrentLiabilities"));
            entity.setAnnualOtherLiabilities(parseLongListToArray(annualReports, "otherNonCurrentLiabilities"));
            entity.setAnnualRetainedEarnings(parseLongListToArray(annualReports, "retainedEarnings"));
            entity.setAnnualTreasuryStock(parseLongListToArray(annualReports, "treasuryStock"));

            // Quarterly
            entity.setQuarterlyFiscalDateEnding(parseDateListToArray(quarterlyReports, "fiscalDateEnding"));
            entity.setQuarterlyTotalAssets(parseLongListToArray(quarterlyReports, "totalAssets"));
            entity.setQuarterlyTotalLiabilities(parseLongListToArray(quarterlyReports, "totalLiabilities"));
            entity.setQuarterlyTotalShareholderEquity(parseLongListToArray(quarterlyReports, "totalShareholderEquity"));
            entity.setQuarterlyCashAndCashEquivalents(parseLongListToArray(quarterlyReports, "cashAndCashEquivalentsAtCarryingValue"));
            entity.setQuarterlyShortTermInvestments(parseLongListToArray(quarterlyReports, "shortTermInvestments"));
            entity.setQuarterlyNetReceivables(parseLongListToArray(quarterlyReports, "currentNetReceivables"));
            entity.setQuarterlyInventory(parseLongListToArray(quarterlyReports, "inventory"));
            entity.setQuarterlyOtherCurrentAssets(parseLongListToArray(quarterlyReports, "otherCurrentAssets"));
            entity.setQuarterlyOtherAssets(parseLongListToArray(quarterlyReports, "otherNonCurrentAssets"));
            entity.setQuarterlyAccountsPayable(parseLongListToArray(quarterlyReports, "currentAccountsPayable"));
            entity.setQuarterlyCurrentDebt(parseLongListToArray(quarterlyReports, "currentDebt"));
            entity.setQuarterlyLongTermDebt(parseLongListToArray(quarterlyReports, "longTermDebt"));
            entity.setQuarterlyOtherCurrentLiabilities(parseLongListToArray(quarterlyReports, "otherCurrentLiabilities"));
            entity.setQuarterlyOtherLiabilities(parseLongListToArray(quarterlyReports, "otherNonCurrentLiabilities"));
            entity.setQuarterlyRetainedEarnings(parseLongListToArray(quarterlyReports, "retainedEarnings"));
            entity.setQuarterlyTreasuryStock(parseLongListToArray(quarterlyReports, "treasuryStock"));

            repository.saveAndFlush(entity);
            logInfo("Saved BalanceSheet for symbol: " + symbol);

        } catch (Exception ex) {
            logError("Failed to process symbol " + symbol + ": " + ex.getMessage());
        }
    }

    /* ================= HELPERS ================= */
    private List<Map<String, String>> sortReportsByDateAsc(List<Map<String, String>> reports) {
        if (reports == null) return Collections.emptyList();
        reports.sort(Comparator.comparing(r -> parseDate(r.get("fiscalDateEnding"))));
        return reports;
    }

    private LocalDate parseDate(String v) {
        return (v == null || v.isBlank()) ? null : LocalDate.parse(v, formatter);
    }

    private LocalDate[] parseDateListToArray(List<Map<String, String>> reports, String key) {
        if (reports == null) return new LocalDate[0];
        return reports.stream().map(r -> parseDate(r.get(key))).toArray(LocalDate[]::new);
    }

    private Long parseLong(String v) {
        try {
            if (v == null || v.isBlank() || "None".equalsIgnoreCase(v)) return 0L;
            return Math.round(Double.parseDouble(v));
        } catch (Exception e) {
            return 0L;
        }
    }

    private Long[] parseLongListToArray(List<Map<String, String>> reports, String key) {
        if (reports == null) return new Long[0];
        return reports.stream().map(r -> parseLong(r.get(key))).toArray(Long[]::new);
    }

    private void logInfo(String msg) {
        System.out.println(LocalDateTime.now().format(logFormatter) + " INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println(LocalDateTime.now().format(logFormatter) + " ERROR: " + msg);
    }
}
