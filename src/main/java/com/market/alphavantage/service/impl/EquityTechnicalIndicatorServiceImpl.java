package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.EquityTechnicalIndicatorDTO;
import com.market.alphavantage.entity.EquityTechnicalIndicator;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.EquityTechnicalIndicatorRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.EquityTechnicalIndicatorService;
import lombok.RequiredArgsConstructor;
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
    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

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
            fetchIndicator("SMA", symbol, "daily", 20, "close");
            fetchIndicator("SMA", symbol, "daily", 50, "close");
            fetchIndicator("SMA", symbol, "daily", 100, "close");
            fetchIndicator("SMA", symbol, "daily", 200, "close");

            // RSI
            fetchIndicator("RSI", symbol, "daily", 14, "close");

            // MACD (special case)
            fetchIndicator("MACD", symbol, "daily", null, "close");

            success.incrementAndGet();
            logInfo("Indicators Processed " + current + "/" + total + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("Indicators Processed " + current + "/" + total + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }

    private void fetchIndicator(String function,
                                String symbol,
                                String interval,
                                Integer timePeriod,
                                String seriesType) {

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StringBuilder url = new StringBuilder(baseUrl)
                .append("?function=").append(function)
                .append("&symbol=").append(symbol.toUpperCase())
                .append("&interval=").append(interval.toLowerCase());

        if (timePeriod != null)
            url.append("&time_period=").append(timePeriod);

        if (seriesType != null)
            url.append("&series_type=").append(seriesType.toLowerCase());

        url.append("&apikey=").append(apiKey);

        Map<String, Object> response = restTemplate.getForObject(url.toString(), Map.class);
        if (response == null || response.isEmpty()) return;

        String key = "Technical Analysis: " + function;
        Map<String, Map<String, String>> series =
                (Map<String, Map<String, String>>) response.get(key);
        if (series == null) return;

        EquityTechnicalIndicator entity = new EquityTechnicalIndicator();

        String id = symbol.toUpperCase()
                + "_" + interval.toLowerCase()
                + "_" + (timePeriod != null ? timePeriod : "NA")
                + "_" + (seriesType != null ? seriesType.toLowerCase() : "NA")
                + "_" + function;

        entity.setId(id);
        entity.setSymbol(symbol.toUpperCase());
        entity.setInterval(interval.toLowerCase());
        entity.setTimePeriod(timePeriod != null ? timePeriod : 0);
        entity.setSeriesType(seriesType != null ? seriesType.toLowerCase() : null);
        entity.setFunction(function); // NEW

        // Sort dates ascending (oldest → newest)
        List<String> sortedDates = series.keySet().stream().sorted().toList();
        LocalDate[] dateArr = new LocalDate[sortedDates.size()];
        Double[] valueArr = new Double[sortedDates.size()];

        for (int i = 0; i < sortedDates.size(); i++) {
            String dateStr = sortedDates.get(i);
            dateArr[i] = LocalDate.parse(dateStr.substring(0, 10));
            String val = series.get(dateStr).get(function);
            valueArr[i] = val != null ? Double.parseDouble(val) : 0.0;
        }

        entity.setDates(dateArr);
        entity.setValues(valueArr); // NEW

        repository.save(entity);

        logInfo("Fetched indicator " + function + " for " + symbol
                + " (" + interval + ", " + (timePeriod != null ? timePeriod : "NA") + ")");
    }

    // Helper
    private Double parseDouble(String val) {
        try {
            return val != null ? Double.parseDouble(val) : 0.0;
        } catch (Exception e) {
            return 0.0;
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
