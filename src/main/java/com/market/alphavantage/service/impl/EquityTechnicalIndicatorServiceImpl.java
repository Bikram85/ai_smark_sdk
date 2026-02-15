package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.EquityTechnicalIndicatorDTO;
import com.market.alphavantage.entity.EquityTechnicalIndicator;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.EquityTechnicalIndicatorRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.EquityTechnicalIndicatorService;
import com.market.alphavantage.service.impl.processor.EquityTechincalIndicatorProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class EquityTechnicalIndicatorServiceImpl implements EquityTechnicalIndicatorService {

    private final EquityTechnicalIndicatorRepository repository;

    private final SymbolRepository symbolRepo;

    @Autowired
    EquityTechincalIndicatorProcessor equityTechincalIndicatorProcessor;



    private final DateTimeFormatter logFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadSMA() {

        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");
        int total = stocks.size();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol ->
                processSymbol(symbol.getSymbol(), processed, success, failed, total)
        );

        logInfo("\n===== SUMMARY =====");
        logInfo("Total symbols : " + total);
        logInfo("Success       : " + success.get());
        logInfo("Failed        : " + failed.get());
    }

    private void processSymbol(String symbol,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {

        int current = processed.incrementAndGet();

        try {
            // Daily SMA
            equityTechincalIndicatorProcessor.fetchIndicator("SMA", symbol, "daily", 20, "close");
            equityTechincalIndicatorProcessor.fetchIndicator("SMA", symbol, "daily", 50, "close");
            equityTechincalIndicatorProcessor.fetchIndicator("SMA", symbol, "daily", 100, "close");
            equityTechincalIndicatorProcessor.fetchIndicator("SMA", symbol, "daily", 200, "close");

            // RSI
            equityTechincalIndicatorProcessor.fetchIndicator("RSI", symbol, "daily", 14, "close");

            // MACD (special case)
            equityTechincalIndicatorProcessor.fetchIndicator("MACD", symbol, "daily", null, "close");

            success.incrementAndGet();
            logInfo("Indicators Processed " + current + "/" + total + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("Indicators Processed " + current + "/" + total + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }





    @Override
    public EquityTechnicalIndicatorDTO getSMA(String symbol,
                                              String interval,
                                              Integer timePeriod,
                                              String seriesType) {

        String id = symbol.toUpperCase()
                + "_SMA_"
                + interval.toLowerCase()
                + "_" + (timePeriod != null ? timePeriod : "NA")
                + "_" + (seriesType != null
                ? seriesType.toLowerCase()
                : "NA");

        EquityTechnicalIndicator e =
                repository.findById(id).orElse(null);

        if (e == null)
            return null;

        return new EquityTechnicalIndicatorDTO(
                e.getId(),
                e.getSymbol(),
                e.getInterval(),
                e.getTimePeriod(),
                e.getSeriesType(),
                e.getFunction(), // Don't forget the new function field
                e.getDates() != null ? Arrays.asList(e.getDates()) : new ArrayList<>(),
                e.getValues() != null ? Arrays.asList(e.getValues()) : new ArrayList<>()
        );
    }

    /* ---------- Logging ---------- */

    private void logInfo(String msg) {
        System.out.println(LocalDateTime.now()
                .format(logFormatter)
                + " INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println(LocalDateTime.now()
                .format(logFormatter)
                + " ERROR: " + msg);
    }
}
