package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.IncomeStatementDTO;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.IncomeStatementRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.IncomeStatementService;
import com.market.alphavantage.service.impl.processor.IncomeStatementProcessor;
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
public class IncomeStatementServiceImpl implements IncomeStatementService {

    @Autowired
    public IncomeStatementProcessor processor;
    private final SymbolRepository symbolRepo;
    private final IncomeStatementRepository repository;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadIncomeStatement() {
        repository.deleteAll();
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");

        int total = stocks.size();
        AtomicInteger processed = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        for (Symbol s : stocks) {
            processSymbol(s.getSymbol(), processed, success, failed, total);
        }

        log("\n===== INCOME STATEMENT SUMMARY =====");
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
    public IncomeStatementDTO getIncomeStatementDTO(String symbol) {
        return processor.getIncFlow(symbol);
    }

    private void log(String message) {
        System.out.println("[" + LocalDateTime.now().format(dtf) + "] INFO: " + message);
    }

    private void logErr(String message) {
        System.err.println("[" + LocalDateTime.now().format(dtf) + "] ERROR: " + message);
    }
}
