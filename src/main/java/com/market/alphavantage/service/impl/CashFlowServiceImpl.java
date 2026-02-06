package com.market.alphavantage.service.impl;


import com.market.alphavantage.dto.CashFlowDTO;
import com.market.alphavantage.entity.CashFlow;
import com.market.alphavantage.repository.CashFlowRepository;
import com.market.alphavantage.service.CashFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CashFlowServiceImpl implements CashFlowService {

    private final CashFlowRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadCashFlow(String symbol) {

        String url = baseUrl + "?function=CASH_FLOW&symbol=" + symbol + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("annualReports")) return;

        List<Map<String, String>> annualReports = (List<Map<String, String>>) response.get("annualReports");
        List<Map<String, String>> quarterlyReports = (List<Map<String, String>>) response.get("quarterlyReports");

        CashFlow entity = new CashFlow();
        entity.setSymbol(symbol);

        // Annual
        entity.setAnnualFiscalDateEnding(parseDateList(annualReports, "fiscalDateEnding"));
        entity.setAnnualOperatingCashflow(parseLongList(annualReports, "operatingCashflow"));
        entity.setAnnualPaymentsForOperatingActivities(parseLongList(annualReports, "paymentsForOperatingActivities"));
        entity.setAnnualProceedsFromOperatingActivities(parseLongList(annualReports, "proceedsFromOperatingActivities"));
        entity.setAnnualChangeInCash(parseLongList(annualReports, "changeInCash"));
        entity.setAnnualCashflowFromInvestment(parseLongList(annualReports, "cashflowFromInvestment"));
        entity.setAnnualCashflowFromFinancing(parseLongList(annualReports, "cashflowFromFinancing"));
        entity.setAnnualProceedsFromRepaymentsOfShortTermDebt(parseLongList(annualReports, "proceedsFromRepaymentsOfShortTermDebt"));
        entity.setAnnualPaymentsForRepurchaseOfCommonStock(parseLongList(annualReports, "paymentsForRepurchaseOfCommonStock"));
        entity.setAnnualPaymentsForRepurchaseOfEquity(parseLongList(annualReports, "paymentsForRepurchaseOfEquity"));
        entity.setAnnualPaymentsForRepurchaseOfPreferredStock(parseLongList(annualReports, "paymentsForRepurchaseOfPreferredStock"));
        entity.setAnnualDividendsPaid(parseLongList(annualReports, "dividendsPaid"));
        entity.setAnnualDividendsPaidOnCommonStock(parseLongList(annualReports, "dividendsPaidOnCommonStock"));
        entity.setAnnualDividendsPaidOnPreferredStock(parseLongList(annualReports, "dividendsPaidOnPreferredStock"));
        entity.setAnnualProceedsFromIssuanceOfCommonStock(parseLongList(annualReports, "proceedsFromIssuanceOfCommonStock"));
        entity.setAnnualProceedsFromIssuanceOfLongTermDebt(parseLongList(annualReports, "proceedsFromIssuanceOfLongTermDebt"));
        entity.setAnnualProceedsFromIssuanceOfPreferredStock(parseLongList(annualReports, "proceedsFromIssuanceOfPreferredStock"));
        entity.setAnnualProceedsFromRepurchaseOfEquity(parseLongList(annualReports, "proceedsFromRepurchaseOfEquity"));
        entity.setAnnualOtherCashflowFromFinancingActivities(parseLongList(annualReports, "otherCashflowFromFinancingActivities"));
        entity.setAnnualNetBorrowings(parseLongList(annualReports, "netBorrowings"));

        // Quarterly
        entity.setQuarterlyFiscalDateEnding(parseDateList(quarterlyReports, "fiscalDateEnding"));
        entity.setQuarterlyOperatingCashflow(parseLongList(quarterlyReports, "operatingCashflow"));
        entity.setQuarterlyPaymentsForOperatingActivities(parseLongList(quarterlyReports, "paymentsForOperatingActivities"));
        entity.setQuarterlyProceedsFromOperatingActivities(parseLongList(quarterlyReports, "proceedsFromOperatingActivities"));
        entity.setQuarterlyChangeInCash(parseLongList(quarterlyReports, "changeInCash"));
        entity.setQuarterlyCashflowFromInvestment(parseLongList(quarterlyReports, "cashflowFromInvestment"));
        entity.setQuarterlyCashflowFromFinancing(parseLongList(quarterlyReports, "cashflowFromFinancing"));
        entity.setQuarterlyProceedsFromRepaymentsOfShortTermDebt(parseLongList(quarterlyReports, "proceedsFromRepaymentsOfShortTermDebt"));
        entity.setQuarterlyPaymentsForRepurchaseOfCommonStock(parseLongList(quarterlyReports, "paymentsForRepurchaseOfCommonStock"));
        entity.setQuarterlyPaymentsForRepurchaseOfEquity(parseLongList(quarterlyReports, "paymentsForRepurchaseOfEquity"));
        entity.setQuarterlyPaymentsForRepurchaseOfPreferredStock(parseLongList(quarterlyReports, "paymentsForRepurchaseOfPreferredStock"));
        entity.setQuarterlyDividendsPaid(parseLongList(quarterlyReports, "dividendsPaid"));
        entity.setQuarterlyDividendsPaidOnCommonStock(parseLongList(quarterlyReports, "dividendsPaidOnCommonStock"));
        entity.setQuarterlyDividendsPaidOnPreferredStock(parseLongList(quarterlyReports, "dividendsPaidOnPreferredStock"));
        entity.setQuarterlyProceedsFromIssuanceOfCommonStock(parseLongList(quarterlyReports, "proceedsFromIssuanceOfCommonStock"));
        entity.setQuarterlyProceedsFromIssuanceOfLongTermDebt(parseLongList(quarterlyReports, "proceedsFromIssuanceOfLongTermDebt"));
        entity.setQuarterlyProceedsFromIssuanceOfPreferredStock(parseLongList(quarterlyReports, "proceedsFromIssuanceOfPreferredStock"));
        entity.setQuarterlyProceedsFromRepurchaseOfEquity(parseLongList(quarterlyReports, "proceedsFromRepurchaseOfEquity"));
        entity.setQuarterlyOtherCashflowFromFinancingActivities(parseLongList(quarterlyReports, "otherCashflowFromFinancingActivities"));
        entity.setQuarterlyNetBorrowings(parseLongList(quarterlyReports, "netBorrowings"));

        repository.save(entity);
    }

    @Override
    public CashFlowDTO getCashFlow(String symbol) {
        CashFlow e = repository.findById(symbol).orElse(null);
        if (e == null) return null;

        return new CashFlowDTO(
                e.getSymbol(),

                e.getAnnualFiscalDateEnding(),
                e.getAnnualOperatingCashflow(),
                e.getAnnualPaymentsForOperatingActivities(),
                e.getAnnualProceedsFromOperatingActivities(),
                e.getAnnualChangeInCash(),
                e.getAnnualCashflowFromInvestment(),
                e.getAnnualCashflowFromFinancing(),
                e.getAnnualProceedsFromRepaymentsOfShortTermDebt(),
                e.getAnnualPaymentsForRepurchaseOfCommonStock(),
                e.getAnnualPaymentsForRepurchaseOfEquity(),
                e.getAnnualPaymentsForRepurchaseOfPreferredStock(),
                e.getAnnualDividendsPaid(),
                e.getAnnualDividendsPaidOnCommonStock(),
                e.getAnnualDividendsPaidOnPreferredStock(),
                e.getAnnualProceedsFromIssuanceOfCommonStock(),
                e.getAnnualProceedsFromIssuanceOfLongTermDebt(),
                e.getAnnualProceedsFromIssuanceOfPreferredStock(),
                e.getAnnualProceedsFromRepurchaseOfEquity(),
                e.getAnnualOtherCashflowFromFinancingActivities(),
                e.getAnnualNetBorrowings(),

                e.getQuarterlyFiscalDateEnding(),
                e.getQuarterlyOperatingCashflow(),
                e.getQuarterlyPaymentsForOperatingActivities(),
                e.getQuarterlyProceedsFromOperatingActivities(),
                e.getQuarterlyChangeInCash(),
                e.getQuarterlyCashflowFromInvestment(),
                e.getQuarterlyCashflowFromFinancing(),
                e.getQuarterlyProceedsFromRepaymentsOfShortTermDebt(),
                e.getQuarterlyPaymentsForRepurchaseOfCommonStock(),
                e.getQuarterlyPaymentsForRepurchaseOfEquity(),
                e.getQuarterlyPaymentsForRepurchaseOfPreferredStock(),
                e.getQuarterlyDividendsPaid(),
                e.getQuarterlyDividendsPaidOnCommonStock(),
                e.getQuarterlyDividendsPaidOnPreferredStock(),
                e.getQuarterlyProceedsFromIssuanceOfCommonStock(),
                e.getQuarterlyProceedsFromIssuanceOfLongTermDebt(),
                e.getQuarterlyProceedsFromIssuanceOfPreferredStock(),
                e.getQuarterlyProceedsFromRepurchaseOfEquity(),
                e.getQuarterlyOtherCashflowFromFinancingActivities(),
                e.getQuarterlyNetBorrowings()
        );
    }

    /* ===== Helper Methods ===== */
    private Long parseLong(String val) { return (val == null || val.isBlank()) ? 0L : Long.valueOf(val); }

    private LocalDate parseDate(String val) { return (val == null || val.isBlank()) ? null : LocalDate.parse(val); }

    private List<Long> parseLongList(List<Map<String, String>> reports, String key) {
        List<Long> list = new ArrayList<>();
        for (Map<String, String> r : reports) list.add(parseLong(r.get(key)));
        return list;
    }

    private List<LocalDate> parseDateList(List<Map<String, String>> reports, String key) {
        List<LocalDate> list = new ArrayList<>();
        for (Map<String, String> r : reports) list.add(parseDate(r.get(key)));
        return list;
    }
}
