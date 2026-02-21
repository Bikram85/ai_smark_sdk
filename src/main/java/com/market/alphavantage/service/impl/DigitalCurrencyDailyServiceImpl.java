package com.market.alphavantage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.alphavantage.dto.DigitalCurrencyDailyDTO;
import com.market.alphavantage.entity.DigitalCurrencyDaily;
import com.market.alphavantage.repository.DigitalCurrencyDailyRepository;
import com.market.alphavantage.service.DigitalCurrencyDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DigitalCurrencyDailyServiceImpl implements DigitalCurrencyDailyService {

    private final DigitalCurrencyDailyRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final String BASE_CURRENCY = "USD";

    private static final List<String> CRYPTO_SYMBOLS = List.of(
            "BTC", "ETH", "USDT", "USDC", "SOL",
            "XRP", "BNB", "ADA", "DOGE", "DOT"
    );

    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadDigitalCurrencyDaily() {
        repository.deleteAll();
        CRYPTO_SYMBOLS.forEach(symbol -> fetchDetails(symbol, BASE_CURRENCY));
    }


    @Override
    public void loadDigitalCurrencyIntraday() {
        CRYPTO_SYMBOLS.forEach(symbol -> fetchAndUpdateCryptoIntraday(symbol, BASE_CURRENCY));
    }

    private void fetchDetails(String symbol, String market) {
        String url = baseUrl
                + "?function=DIGITAL_CURRENCY_DAILY"
                + "&symbol=" + symbol.toUpperCase()
                + "&market=" + market.toUpperCase()
                + "&apikey=" + apiKey;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.isEmpty()) {
                logError("No response for " + symbol + " -> " + market);
                return;
            }

            Map<String, Map<String, String>> series =
                    (Map<String, Map<String, String>>) response.get("Time Series (Digital Currency Daily)");
            if (series == null || series.isEmpty()) {
                logError("No time series data for " + symbol + " -> " + market);
                return;
            }

            // Sort dates ascending (oldest first)
            List<String> sortedDates = new ArrayList<>(series.keySet());
            sortedDates.sort(String::compareTo);

            DigitalCurrencyDaily entity = new DigitalCurrencyDaily();
            String id = symbol.toUpperCase() + "_" + market.toUpperCase();
            entity.setId(id);
            entity.setSymbol(symbol.toUpperCase());
            entity.setMarket(market.toUpperCase());

            int size = sortedDates.size();
            LocalDate[] tradeDates = new LocalDate[size];
            Double[] open = new Double[size];
            Double[] high = new Double[size];
            Double[] low = new Double[size];
            Double[] close = new Double[size];
            Double[] volume = new Double[size];
            Double[] marketCap = new Double[size];

            for (int i = 0; i < size; i++) {
                String date = sortedDates.get(i);
                Map<String, String> values = series.get(date);

                tradeDates[i] = parseDate(date);

                // Use "1a. open" if exists, otherwise fallback to "1. open"
                open[i] = parseDouble(values.getOrDefault("1a. open (" + market.toUpperCase() + ")", values.get("1. open")));
                high[i] = parseDouble(values.getOrDefault("2a. high (" + market.toUpperCase() + ")", values.get("2. high")));
                low[i] = parseDouble(values.getOrDefault("3a. low (" + market.toUpperCase() + ")", values.get("3. low")));
                close[i] = parseDouble(values.getOrDefault("4a. close (" + market.toUpperCase() + ")", values.get("4. close")));
                volume[i] = parseDouble(values.get("5. volume"));
                marketCap[i] = parseDouble(values.getOrDefault("6. market cap (" + market.toUpperCase() + ")", "0"));
            }

            entity.setTradeDate(tradeDates);
            entity.setOpen(open);
            entity.setHigh(high);
            entity.setLow(low);
            entity.setClose(close);
            entity.setVolume(volume);
            entity.setMarketCap(marketCap);

            repository.save(entity);
            logInfo("Saved digital currency daily for " + id);

        } catch (Exception ex) {
            logError("Failed to fetch digital currency daily for " + symbol + " -> " + market
                    + ". Reason: " + ex.getMessage());
        }
    }


    public void fetchAndUpdateCryptoIntraday(String symbol, String market) {

        try {
            String url = baseUrl
                    + "?function=CRYPTO_INTRADAY"
                    + "&symbol=" + symbol.toUpperCase()
                    + "&market=" + market.toUpperCase()
                    + "&interval=5min"
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isBlank()) {
                logError("Empty response for crypto intraday " + symbol);
                return;
            }

            JsonNode root = new ObjectMapper().readTree(response);

            // 🔥 API limit protection
            if (root.has("Note") || root.has("Error Message")) {
                logError("API limit hit for crypto intraday " + symbol);
                return;
            }

            JsonNode meta = root.get("Meta Data");
            JsonNode series = root.get("Time Series Crypto (5min)");

            if (meta == null || series == null) {
                logError("Missing Meta Data or Time Series for " + symbol);
                return;
            }

            // handle possible variations in key names
            JsonNode lastRefreshedNode = meta.get("5. Last Refreshed");
            if (lastRefreshedNode == null) lastRefreshedNode = meta.get("6. Last Refreshed");

            if (lastRefreshedNode == null) {
                logError("Missing Last Refreshed for " + symbol);
                return;
            }

            String latestTimestamp = lastRefreshedNode.asText();
            JsonNode candle = series.get(latestTimestamp);

            if (candle == null) {
                logError("No candle found for latest timestamp " + latestTimestamp + " for " + symbol);
                return;
            }

            // Safely parse numeric values
            double open = candle.has("1. open") ? candle.get("1. open").asDouble() : 0.0;
            double high = candle.has("2. high") ? candle.get("2. high").asDouble() : 0.0;
            double low = candle.has("3. low") ? candle.get("3. low").asDouble() : 0.0;
            double close = candle.has("4. close") ? candle.get("4. close").asDouble() : 0.0;
            double volume = candle.has("5. volume") ? candle.get("5. volume").asDouble() : 0.0;

            LocalDate today = LocalDate.parse(latestTimestamp.substring(0, 10));

            String id = symbol.toUpperCase() + "_" + market.toUpperCase();
            Optional<DigitalCurrencyDaily> optional = repository.findById(id);

            if (optional.isEmpty()) {
                logError("No existing entity found for " + id);
                return;
            }

            DigitalCurrencyDaily entity = optional.get();

            List<LocalDate> dates = new ArrayList<>(Arrays.asList(entity.getTradeDate()));
            List<Double> opens = new ArrayList<>(Arrays.asList(entity.getOpen()));
            List<Double> highs = new ArrayList<>(Arrays.asList(entity.getHigh()));
            List<Double> lows = new ArrayList<>(Arrays.asList(entity.getLow()));
            List<Double> closes = new ArrayList<>(Arrays.asList(entity.getClose()));
            List<Double> volumes = new ArrayList<>(Arrays.asList(entity.getVolume()));
            List<Double> marketCaps = new ArrayList<>(Arrays.asList(entity.getMarketCap()));

            int lastIndex = dates.size() - 1;

            // ✅ Update existing today's record if present
            if (!dates.isEmpty() && dates.get(lastIndex).equals(today)) {
                highs.set(lastIndex, Math.max(highs.get(lastIndex), high));
                lows.set(lastIndex, Math.min(lows.get(lastIndex), low));
                closes.set(lastIndex, close);

                // Increment volume
                volumes.set(lastIndex, volumes.get(lastIndex) + volume);

            } else {
                // Append new trading day
                dates.add(today);
                opens.add(open);
                highs.add(high);
                lows.add(low);
                closes.add(close);
                volumes.add(volume);
                marketCaps.add(0.0); // intraday doesn't return market cap
            }

            // Save updated entity
            entity.setTradeDate(dates.toArray(new LocalDate[0]));
            entity.setOpen(opens.toArray(new Double[0]));
            entity.setHigh(highs.toArray(new Double[0]));
            entity.setLow(lows.toArray(new Double[0]));
            entity.setClose(closes.toArray(new Double[0]));
            entity.setVolume(volumes.toArray(new Double[0]));
            entity.setMarketCap(marketCaps.toArray(new Double[0]));

            repository.save(entity);

            logInfo("Updated crypto intraday for " + id);

        } catch (Exception ex) {
            logError("Failed crypto intraday for " + symbol + " -> " + market
                    + ". Reason: " + ex.getMessage());
        }
    }
    /** Parse string to double, remove commas and round to 2 decimals */
    private Double parseDouble(String val) {
        try {
            if (val == null || val.isBlank()) return 0.0;
            val = val.replaceAll(",", "").trim();
            double d = Double.parseDouble(val);
            return Math.round(d * 100.0) / 100.0;
        } catch (Exception ex) {
            return 0.0;
        }
    }

    @Override
    public List<DigitalCurrencyDailyDTO> getAllDigitalCurrencyDaily() {
        List<DigitalCurrencyDaily> entities = repository.findAll();

        if (entities.isEmpty()) {
            logInfo("No digital currency daily data found");
            return new ArrayList<>();
        }

        return entities.stream()
                .map(e -> new DigitalCurrencyDailyDTO(
                        e.getId(),
                        e.getSymbol(),
                        e.getMarket(),
                        e.getTradeDate() != null ? List.of(e.getTradeDate()) : new ArrayList<>(),
                        e.getOpen() != null ? List.of(e.getOpen()) : new ArrayList<>(),
                        e.getHigh() != null ? List.of(e.getHigh()) : new ArrayList<>(),
                        e.getLow() != null ? List.of(e.getLow()) : new ArrayList<>(),
                        e.getClose() != null ? List.of(e.getClose()) : new ArrayList<>(),
                        e.getVolume() != null ? List.of(e.getVolume()) : new ArrayList<>(),
                        e.getMarketCap() != null ? List.of(e.getMarketCap()) : new ArrayList<>()
                ))
                .collect(Collectors.toList());
    }







    private LocalDate parseDate(String val) {
        try {
            return val == null || val.isBlank() ? null : LocalDate.parse(val);
        } catch (Exception ex) {
            return null;
        }
    }



    /* ===== Logging helpers ===== */
    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] ERROR: " + msg);
    }
}
