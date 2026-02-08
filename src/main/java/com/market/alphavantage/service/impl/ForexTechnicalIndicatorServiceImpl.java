package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.ForexTechnicalIndicatorDTO;
import com.market.alphavantage.entity.ForexTechnicalIndicator;
import com.market.alphavantage.repository.ForexTechnicalIndicatorRepository;
import com.market.alphavantage.service.ForexTechnicalIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class ForexTechnicalIndicatorServiceImpl implements ForexTechnicalIndicatorService {

    private final ForexTechnicalIndicatorRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final List<Integer> SMA_PERIODS = List.of(20, 50, 100, 200);
    private static final String DEFAULT_INTERVAL = "daily";
    private static final String DEFAULT_SERIES_TYPE = "close";

    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadSMA() {
        List<String> symbols = List.of(
                "JPYUSD", "AEDUSD", "CADUSD", "CHFUSD",
                "EURUSD", "GBPUSD", "INRUSD", "SARUSD"
        );

        int total = symbols.size();
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        symbols.forEach(symbol -> processSymbol(symbol, processed, success, failed, total));

        logInfo("\n===== FOREX SUMMARY =====");
        logInfo("Total pairs : " + total);
        logInfo("Success     : " + success.get());
        logInfo("Failed      : " + failed.get());
    }

    private void processSymbol(String symbol,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {
        int current = processed.incrementAndGet();

        try {
            // ---- SMA ----
            for (Integer period : SMA_PERIODS) {
                fetchIndicator("SMA", symbol, DEFAULT_INTERVAL, period, DEFAULT_SERIES_TYPE);
            }

            // ---- RSI ----
            fetchIndicator("RSI", symbol, DEFAULT_INTERVAL, 14, DEFAULT_SERIES_TYPE);

            // ---- MACD ----
            fetchIndicator("MACD", symbol, DEFAULT_INTERVAL, null, DEFAULT_SERIES_TYPE);

            success.incrementAndGet();
            logInfo("Forex Processed " + current + "/" + total + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("Forex Processed " + current + "/" + total + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }

    private void fetchIndicator(String function,
                                String symbol,
                                String interval,
                                Integer timePeriod,
                                String seriesType) {

        StringBuilder url = new StringBuilder(baseUrl)
                .append("?function=").append(function)
                .append("&symbol=").append(symbol.toUpperCase())
                .append("&interval=").append(interval.toLowerCase());

        if (timePeriod != null) url.append("&time_period=").append(timePeriod);
        if (seriesType != null) url.append("&series_type=").append(seriesType.toLowerCase());
        url.append("&apikey=").append(apiKey);

        Map<String, Object> response = restTemplate.getForObject(url.toString(), Map.class);
        if (response == null || response.isEmpty()) return;

        String key = "Technical Analysis: " + function;
        Map<String, Map<String, String>> series = (Map<String, Map<String, String>>) response.get(key);
        if (series == null) return;

        ForexTechnicalIndicator entity = new ForexTechnicalIndicator();
        String id = symbol.toUpperCase()
                + "_" + function
                + "_" + interval.toLowerCase()
                + "_" + (timePeriod != null ? timePeriod : "NA")
                + "_" + seriesType.toLowerCase();

        entity.setId(id);
        entity.setSymbol(symbol.toUpperCase());
        entity.setInterval(interval.toLowerCase());
        entity.setTimePeriod(timePeriod);
        entity.setSeriesType(seriesType.toLowerCase());

        List<LocalDate> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        series.forEach((date, valuesMap) -> {
            dates.add(parseDate(date));
            String val = valuesMap.get(function);
            if (val == null && function.equals("MACD")) val = valuesMap.get("MACD");
            values.add(parseDouble(val));
        });

        // Convert to arrays for single-row storage
        entity.setDates(dates.toArray(new LocalDate[0]));
        entity.setSmaValues(values.toArray(new Double[0]));

        repository.save(entity);
        logInfo("Saved " + function + " for " + symbol + " (" + interval + ", " + (timePeriod != null ? timePeriod : "NA") + ")");
    }

    @Override
    public ForexTechnicalIndicatorDTO getSMA(String symbol,
                                             String interval,
                                             Integer timePeriod,
                                             String seriesType) {

        String id = symbol.toUpperCase()
                + "_SMA_"
                + interval.toLowerCase()
                + "_" + timePeriod
                + "_" + seriesType.toLowerCase();

        ForexTechnicalIndicator e = repository.findById(id).orElse(null);
        if (e == null) {
            logInfo("No Forex indicator found for " + symbol);
            return null;
        }

        // Convert arrays to lists for DTO
        List<LocalDate> dates = e.getDates() != null ? List.of(e.getDates()) : new ArrayList<>();
        List<Double> smaValues = e.getSmaValues() != null ? List.of(e.getSmaValues()) : new ArrayList<>();

        return new ForexTechnicalIndicatorDTO(
                e.getId(),
                e.getSymbol(),
                e.getInterval(),
                e.getTimePeriod(),
                e.getSeriesType(),
                dates,
                smaValues
        );
    }

    /* ===== Helpers ===== */
    private LocalDate parseDate(String val) {
        try {
            return val == null || val.isBlank() ? null : LocalDate.parse(val);
        } catch (Exception ex) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return val == null || val.isBlank() ? 0.0 : Double.valueOf(val);
        } catch (Exception ex) {
            return 0.0;
        }
    }

    /* ===== Logger ===== */
    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] ERROR: " + msg);
    }
}
