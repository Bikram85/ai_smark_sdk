package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.CashFlowDTO;
import com.market.alphavantage.entity.CashFlow;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.CashFlowRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.CashFlowService;
import com.market.alphavantage.service.impl.processor.CashFlowProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class CashFlowServiceImpl implements CashFlowService {
   @Autowired
    public CashFlowProcessor processor;
    private final SymbolRepository symbolRepo;
    private final CashFlowRepository repository;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadCashFlow() {
        repository.deleteAll();
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");

        AtomicInteger processed = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        int total = stocks.size();

        for (Symbol s : stocks) {
            processSymbol(s.getSymbol(), processed, success, failed, total);
        }

        log("===== CASHFLOW SUMMARY =====");
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
            processor.processSymbol(symbol);  // fetch & save immediately
            success.incrementAndGet();
            log(timestamp + " | Processed " + current + "/" + total + " SUCCESS: " + symbol);
        } catch (Exception ex) {
            failed.incrementAndGet();
            logErr(timestamp + " | Processed " + current + "/" + total + " FAILED: " + symbol + " -> " + ex.getMessage());
        }
    }

    @Override
    public CashFlowDTO getCashFlow(String symbol) {
        return processor.getCashFlow(symbol);
    }

    @Override
    public void fetchDetails(String symbol) {
        processor.processSymbol(symbol); // delegate to processor
    }

    private void log(String msg) {
        System.out.println(LocalDateTime.now().format(dtf) + " | " + msg);
    }

    private void logErr(String msg) {
        System.err.println(LocalDateTime.now().format(dtf) + " | " + msg);
    }
}
