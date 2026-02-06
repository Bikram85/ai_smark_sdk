package com.market.alphavantage.service.impl;


import com.market.alphavantage.dto.BalanceSheetDTO;
import com.market.alphavantage.entity.BalanceSheet;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.BalanceSheetRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.BalanceSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceSheetServiceImpl implements BalanceSheetService {

    private final BalanceSheetRepository repository;
    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadBalanceSheet() {

        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");

        int total = stocks.size();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol -> {
            processSymbol(symbol.getSymbol(), "Stock",
                    processed, success, failed, total);
        });

        System.out.println("\n===== SUMMARY =====");
        System.out.println("Total symbols : " + total);
        System.out.println("Success       : " + success.get());
        System.out.println("Failed        : " + failed.get());
    }

    private void processSymbol(String symbol,
                               String type,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {

        int current = processed.incrementAndGet();

        try {
            fetchDetails(symbol);
            success.incrementAndGet();

            System.out.println("Balance Details Processed "
                    + current + "/" + total
                    + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();

            System.err.println("Balance Details Processed "
                    + current + "/" + total
                    + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }



    public void fetchDetails(String symbol) {

        String url = baseUrl +
                "?function=BALANCE_SHEET" +
                "&symbol=" + symbol +
                "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("annualReports")) return;

        List<Map<String, String>> annualReports = (List<Map<String, String>>) response.get("annualReports");
        List<Map<String, String>> quarterlyReports = (List<Map<String, String>>) response.get("quarterlyReports");

        BalanceSheet entity = new BalanceSheet();
        entity.setSymbol(symbol);

        /* --- Annual --- */
        List<LocalDate> annDates = new ArrayList<>();
        List<Long> annTotalAssets = new ArrayList<>();
        List<Long> annTotalLiabilities = new ArrayList<>();
        List<Long> annTotalEquity = new ArrayList<>();
        List<Long> annCash = new ArrayList<>();
        List<Long> annShortTermInvest = new ArrayList<>();
        List<Long> annNetReceivables = new ArrayList<>();
        List<Long> annInventory = new ArrayList<>();
        List<Long> annOtherCurrentAssets = new ArrayList<>();
        List<Long> annOtherAssets = new ArrayList<>();
        List<Long> annAcctsPayable = new ArrayList<>();
        List<Long> annCurrentDebt = new ArrayList<>();
        List<Long> annLongTermDebt = new ArrayList<>();
        List<Long> annOtherCurrentLiabilities = new ArrayList<>();
        List<Long> annOtherLiabilities = new ArrayList<>();
        List<Long> annRetainedEarnings = new ArrayList<>();
        List<Long> annTreasuryStock = new ArrayList<>();

        for (Map<String, String> r : annualReports) {
            annDates.add(parseDate(r.get("fiscalDateEnding")));
            annTotalAssets.add(parseLong(r.get("totalAssets")));
            annTotalLiabilities.add(parseLong(r.get("totalLiabilities")));
            annTotalEquity.add(parseLong(r.get("totalShareholderEquity")));
            annCash.add(parseLong(r.get("cashAndCashEquivalentsAtCarryingValue")));
            annShortTermInvest.add(parseLong(r.get("shortTermInvestments")));
            annNetReceivables.add(parseLong(r.get("netReceivables")));
            annInventory.add(parseLong(r.get("inventory")));
            annOtherCurrentAssets.add(parseLong(r.get("otherCurrentAssets")));
            annOtherAssets.add(parseLong(r.get("otherAssets")));
            annAcctsPayable.add(parseLong(r.get("accountsPayable")));
            annCurrentDebt.add(parseLong(r.get("currentDebt")));
            annLongTermDebt.add(parseLong(r.get("longTermDebt")));
            annOtherCurrentLiabilities.add(parseLong(r.get("otherCurrentLiabilities")));
            annOtherLiabilities.add(parseLong(r.get("otherLiabilities")));
            annRetainedEarnings.add(parseLong(r.get("retainedEarnings")));
            annTreasuryStock.add(parseLong(r.get("treasuryStock")));
        }

        entity.setAnnualFiscalDateEnding(annDates);
        entity.setAnnualTotalAssets(annTotalAssets);
        entity.setAnnualTotalLiabilities(annTotalLiabilities);
        entity.setAnnualTotalShareholderEquity(annTotalEquity);
        entity.setAnnualCashAndCashEquivalents(annCash);
        entity.setAnnualShortTermInvestments(annShortTermInvest);
        entity.setAnnualNetReceivables(annNetReceivables);
        entity.setAnnualInventory(annInventory);
        entity.setAnnualOtherCurrentAssets(annOtherCurrentAssets);
        entity.setAnnualOtherAssets(annOtherAssets);
        entity.setAnnualAccountsPayable(annAcctsPayable);
        entity.setAnnualCurrentDebt(annCurrentDebt);
        entity.setAnnualLongTermDebt(annLongTermDebt);
        entity.setAnnualOtherCurrentLiabilities(annOtherCurrentLiabilities);
        entity.setAnnualOtherLiabilities(annOtherLiabilities);
        entity.setAnnualRetainedEarnings(annRetainedEarnings);
        entity.setAnnualTreasuryStock(annTreasuryStock);

        /* --- Quarterly --- */
        List<LocalDate> qDates = new ArrayList<>();
        List<Long> qTotalAssets = new ArrayList<>();
        List<Long> qTotalLiabilities = new ArrayList<>();
        List<Long> qTotalEquity = new ArrayList<>();
        List<Long> qCash = new ArrayList<>();
        List<Long> qShortTermInvest = new ArrayList<>();
        List<Long> qNetReceivables = new ArrayList<>();
        List<Long> qInventory = new ArrayList<>();
        List<Long> qOtherCurrentAssets = new ArrayList<>();
        List<Long> qOtherAssets = new ArrayList<>();
        List<Long> qAcctsPayable = new ArrayList<>();
        List<Long> qCurrentDebt = new ArrayList<>();
        List<Long> qLongTermDebt = new ArrayList<>();
        List<Long> qOtherCurrentLiabilities = new ArrayList<>();
        List<Long> qOtherLiabilities = new ArrayList<>();
        List<Long> qRetainedEarnings = new ArrayList<>();
        List<Long> qTreasuryStock = new ArrayList<>();

        for (Map<String, String> r : quarterlyReports) {
            qDates.add(parseDate(r.get("fiscalDateEnding")));
            qTotalAssets.add(parseLong(r.get("totalAssets")));
            qTotalLiabilities.add(parseLong(r.get("totalLiabilities")));
            qTotalEquity.add(parseLong(r.get("totalShareholderEquity")));
            qCash.add(parseLong(r.get("cashAndCashEquivalentsAtCarryingValue")));
            qShortTermInvest.add(parseLong(r.get("shortTermInvestments")));
            qNetReceivables.add(parseLong(r.get("netReceivables")));
            qInventory.add(parseLong(r.get("inventory")));
            qOtherCurrentAssets.add(parseLong(r.get("otherCurrentAssets")));
            qOtherAssets.add(parseLong(r.get("otherAssets")));
            qAcctsPayable.add(parseLong(r.get("accountsPayable")));
            qCurrentDebt.add(parseLong(r.get("currentDebt")));
            qLongTermDebt.add(parseLong(r.get("longTermDebt")));
            qOtherCurrentLiabilities.add(parseLong(r.get("otherCurrentLiabilities")));
            qOtherLiabilities.add(parseLong(r.get("otherLiabilities")));
            qRetainedEarnings.add(parseLong(r.get("retainedEarnings")));
            qTreasuryStock.add(parseLong(r.get("treasuryStock")));
        }

        entity.setQuarterlyFiscalDateEnding(qDates);
        entity.setQuarterlyTotalAssets(qTotalAssets);
        entity.setQuarterlyTotalLiabilities(qTotalLiabilities);
        entity.setQuarterlyTotalShareholderEquity(qTotalEquity);
        entity.setQuarterlyCashAndCashEquivalents(qCash);
        entity.setQuarterlyShortTermInvestments(qShortTermInvest);
        entity.setQuarterlyNetReceivables(qNetReceivables);
        entity.setQuarterlyInventory(qInventory);
        entity.setQuarterlyOtherCurrentAssets(qOtherCurrentAssets);
        entity.setQuarterlyOtherAssets(qOtherAssets);
        entity.setQuarterlyAccountsPayable(qAcctsPayable);
        entity.setQuarterlyCurrentDebt(qCurrentDebt);
        entity.setQuarterlyLongTermDebt(qLongTermDebt);
        entity.setQuarterlyOtherCurrentLiabilities(qOtherCurrentLiabilities);
        entity.setQuarterlyOtherLiabilities(qOtherLiabilities);
        entity.setQuarterlyRetainedEarnings(qRetainedEarnings);
        entity.setQuarterlyTreasuryStock(qTreasuryStock);

        repository.save(entity);
    }

    @Override
    public BalanceSheetDTO getBalanceSheet(String symbol) {
        BalanceSheet e = repository.findById(symbol).orElse(null);
        if (e == null) return null;

        return new BalanceSheetDTO(
                e.getSymbol(),
                e.getAnnualFiscalDateEnding(),
                e.getAnnualTotalAssets(),
                e.getAnnualTotalLiabilities(),
                e.getAnnualTotalShareholderEquity(),
                e.getAnnualCashAndCashEquivalents(),
                e.getAnnualShortTermInvestments(),
                e.getAnnualNetReceivables(),
                e.getAnnualInventory(),
                e.getAnnualOtherCurrentAssets(),
                e.getAnnualOtherAssets(),
                e.getAnnualAccountsPayable(),
                e.getAnnualCurrentDebt(),
                e.getAnnualLongTermDebt(),
                e.getAnnualOtherCurrentLiabilities(),
                e.getAnnualOtherLiabilities(),
                e.getAnnualRetainedEarnings(),
                e.getAnnualTreasuryStock(),

                e.getQuarterlyFiscalDateEnding(),
                e.getQuarterlyTotalAssets(),
                e.getQuarterlyTotalLiabilities(),
                e.getQuarterlyTotalShareholderEquity(),
                e.getQuarterlyCashAndCashEquivalents(),
                e.getQuarterlyShortTermInvestments(),
                e.getQuarterlyNetReceivables(),
                e.getQuarterlyInventory(),
                e.getQuarterlyOtherCurrentAssets(),
                e.getQuarterlyOtherAssets(),
                e.getQuarterlyAccountsPayable(),
                e.getQuarterlyCurrentDebt(),
                e.getQuarterlyLongTermDebt(),
                e.getQuarterlyOtherCurrentLiabilities(),
                e.getQuarterlyOtherLiabilities(),
                e.getQuarterlyRetainedEarnings(),
                e.getQuarterlyTreasuryStock()
        );
    }

    /* === Helpers === */
    private Long parseLong(String v) { return (v == null || v.isBlank()) ? 0L : Long.valueOf(v); }
    private LocalDate parseDate(String v) { return (v == null || v.isBlank()) ? null : LocalDate.parse(v); }
}