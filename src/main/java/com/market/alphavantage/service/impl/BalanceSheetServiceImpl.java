package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.BalanceSheetDTO;
import com.market.alphavantage.entity.BalanceSheet;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.BalanceSheetRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.BalanceSheetService;
import com.market.alphavantage.service.impl.processor.BalanceSheetProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceSheetServiceImpl implements BalanceSheetService {

    private final BalanceSheetRepository repository;
    private final SymbolRepository symbolRepo;

    @Autowired
    private BalanceSheetProcessor processor;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /* ================= LOAD ================= */
    @Override
    public void loadBalanceSheet() {
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");

        AtomicInteger processed = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        int total = stocks.size();

        for (Symbol s : stocks) {
            processSymbol(s.getSymbol(), processed, success, failed, total);
        }

        log("===== loadBalanceSheet SUMMARY =====");
        log("Total symbols : " + total);
        log("Success       : " + success.get());
        log("Failed        : " + failed.get());
    }

    private void processSymbol(String symbol,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {

        int current = processed.incrementAndGet();
        String timestamp = LocalDateTime.now().format(dtf);

        try {
            processor.processSymbol(symbol);
            success.incrementAndGet();
            log(timestamp + " | Processed " + current + "/" + total + " SUCCESS: " + symbol);
        } catch (Exception ex) {
            failed.incrementAndGet();
            logErr(timestamp + " | Processed " + current + "/" + total + " FAILED: " + symbol + " -> " + ex.getMessage());
        }
    }

    @Override
    public BalanceSheetDTO getBalanceSheet(String symbol) {
        BalanceSheet e = repository.findById(symbol).orElse(null);
        if (e == null) return null;

        return new BalanceSheetDTO(
                e.getSymbol(),
                e.getAnnualFiscalDateEnding() != null ? Arrays.asList(e.getAnnualFiscalDateEnding()) : List.of(),
                e.getAnnualTotalAssets() != null ? Arrays.asList(e.getAnnualTotalAssets()) : List.of(),
                e.getAnnualTotalLiabilities() != null ? Arrays.asList(e.getAnnualTotalLiabilities()) : List.of(),
                e.getAnnualTotalShareholderEquity() != null ? Arrays.asList(e.getAnnualTotalShareholderEquity()) : List.of(),
                e.getAnnualCashAndCashEquivalents() != null ? Arrays.asList(e.getAnnualCashAndCashEquivalents()) : List.of(),
                e.getAnnualShortTermInvestments() != null ? Arrays.asList(e.getAnnualShortTermInvestments()) : List.of(),
                e.getAnnualNetReceivables() != null ? Arrays.asList(e.getAnnualNetReceivables()) : List.of(),
                e.getAnnualInventory() != null ? Arrays.asList(e.getAnnualInventory()) : List.of(),
                e.getAnnualOtherCurrentAssets() != null ? Arrays.asList(e.getAnnualOtherCurrentAssets()) : List.of(),
                e.getAnnualOtherAssets() != null ? Arrays.asList(e.getAnnualOtherAssets()) : List.of(),
                e.getAnnualAccountsPayable() != null ? Arrays.asList(e.getAnnualAccountsPayable()) : List.of(),
                e.getAnnualCurrentDebt() != null ? Arrays.asList(e.getAnnualCurrentDebt()) : List.of(),
                e.getAnnualLongTermDebt() != null ? Arrays.asList(e.getAnnualLongTermDebt()) : List.of(),
                e.getAnnualOtherCurrentLiabilities() != null ? Arrays.asList(e.getAnnualOtherCurrentLiabilities()) : List.of(),
                e.getAnnualOtherLiabilities() != null ? Arrays.asList(e.getAnnualOtherLiabilities()) : List.of(),
                e.getAnnualRetainedEarnings() != null ? Arrays.asList(e.getAnnualRetainedEarnings()) : List.of(),
                e.getAnnualTreasuryStock() != null ? Arrays.asList(e.getAnnualTreasuryStock()) : List.of(),

                e.getQuarterlyFiscalDateEnding() != null ? Arrays.asList(e.getQuarterlyFiscalDateEnding()) : List.of(),
                e.getQuarterlyTotalAssets() != null ? Arrays.asList(e.getQuarterlyTotalAssets()) : List.of(),
                e.getQuarterlyTotalLiabilities() != null ? Arrays.asList(e.getQuarterlyTotalLiabilities()) : List.of(),
                e.getQuarterlyTotalShareholderEquity() != null ? Arrays.asList(e.getQuarterlyTotalShareholderEquity()) : List.of(),
                e.getQuarterlyCashAndCashEquivalents() != null ? Arrays.asList(e.getQuarterlyCashAndCashEquivalents()) : List.of(),
                e.getQuarterlyShortTermInvestments() != null ? Arrays.asList(e.getQuarterlyShortTermInvestments()) : List.of(),
                e.getQuarterlyNetReceivables() != null ? Arrays.asList(e.getQuarterlyNetReceivables()) : List.of(),
                e.getQuarterlyInventory() != null ? Arrays.asList(e.getQuarterlyInventory()) : List.of(),
                e.getQuarterlyOtherCurrentAssets() != null ? Arrays.asList(e.getQuarterlyOtherCurrentAssets()) : List.of(),
                e.getQuarterlyOtherAssets() != null ? Arrays.asList(e.getQuarterlyOtherAssets()) : List.of(),
                e.getQuarterlyAccountsPayable() != null ? Arrays.asList(e.getQuarterlyAccountsPayable()) : List.of(),
                e.getQuarterlyCurrentDebt() != null ? Arrays.asList(e.getQuarterlyCurrentDebt()) : List.of(),
                e.getQuarterlyLongTermDebt() != null ? Arrays.asList(e.getQuarterlyLongTermDebt()) : List.of(),
                e.getQuarterlyOtherCurrentLiabilities() != null ? Arrays.asList(e.getQuarterlyOtherCurrentLiabilities()) : List.of(),
                e.getQuarterlyOtherLiabilities() != null ? Arrays.asList(e.getQuarterlyOtherLiabilities()) : List.of(),
                e.getQuarterlyRetainedEarnings() != null ? Arrays.asList(e.getQuarterlyRetainedEarnings()) : List.of(),
                e.getQuarterlyTreasuryStock() != null ? Arrays.asList(e.getQuarterlyTreasuryStock()) : List.of()
        );
    }

    private void log(String msg) {
        System.out.println(LocalDateTime.now().format(dtf) + " | " + msg);
    }

    private void logErr(String msg) {
        System.err.println(LocalDateTime.now().format(dtf) + " | " + msg);
    }
}
