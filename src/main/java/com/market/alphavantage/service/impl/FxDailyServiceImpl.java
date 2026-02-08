package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.FxDailyDTO;
import com.market.alphavantage.entity.FxDaily;
import com.market.alphavantage.repository.FxDailyRepository;
import com.market.alphavantage.service.FxDailyService;
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

@Service
@RequiredArgsConstructor
@Transactional
public class FxDailyServiceImpl implements FxDailyService {

    private final FxDailyRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final String BASE_CURRENCY = "USD";

    private static final List<String> FX_SYMBOLS = List.of(
            "JPY", "AED", "CAD", "CHF", "EUR", "GBP", "INR", "RUB", "SAR"
    );

    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadFxDaily() {
        FX_SYMBOLS.forEach(symbol -> fetchDetails(symbol, BASE_CURRENCY));
    }

    private void fetchDetails(String fromSymbol, String toSymbol) {
        String url = baseUrl
                + "?function=FX_DAILY"
                + "&from_symbol=" + fromSymbol.toUpperCase()
                + "&to_symbol=" + toSymbol.toUpperCase()
                + "&outputsize=full"
                + "&apikey=" + apiKey;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.isEmpty()) {
                logError("No response for " + fromSymbol + " -> " + toSymbol);
                return;
            }

            Map<String, Map<String, String>> series =
                    (Map<String, Map<String, String>>) response.get("Time Series FX (Daily)");
            if (series == null || series.isEmpty()) {
                logError("No series data for " + fromSymbol + " -> " + toSymbol);
                return;
            }

            // Sort entries by date ascending (oldest first)
            List<Map.Entry<String, Map<String, String>>> sortedEntries = series.entrySet().stream()
                    .filter(e -> parseDate(e.getKey()) != null)
                    .sorted((e1, e2) -> parseDate(e1.getKey()).compareTo(parseDate(e2.getKey())))
                    .toList();

            FxDaily entity = new FxDaily();
            String id = fromSymbol.toUpperCase() + "_" + toSymbol.toUpperCase();
            entity.setId(id);
            entity.setFromSymbol(fromSymbol.toUpperCase());
            entity.setToSymbol(toSymbol.toUpperCase());

            // Convert sorted entries to arrays
            LocalDate[] tradeDates = sortedEntries.stream()
                    .map(e -> parseDate(e.getKey()))
                    .toArray(LocalDate[]::new);

            Double[] opens = sortedEntries.stream()
                    .map(e -> parseDouble(e.getValue().get("1. open")))
                    .toArray(Double[]::new);

            Double[] highs = sortedEntries.stream()
                    .map(e -> parseDouble(e.getValue().get("2. high")))
                    .toArray(Double[]::new);

            Double[] lows = sortedEntries.stream()
                    .map(e -> parseDouble(e.getValue().get("3. low")))
                    .toArray(Double[]::new);

            Double[] closes = sortedEntries.stream()
                    .map(e -> parseDouble(e.getValue().get("4. close")))
                    .toArray(Double[]::new);

            entity.setTradeDate(tradeDates);
            entity.setOpen(opens);
            entity.setHigh(highs);
            entity.setLow(lows);
            entity.setClose(closes);

            repository.save(entity);
            logInfo("Saved FX daily data for " + id);

        } catch (Exception ex) {
            logError("Failed to fetch FX daily for " + fromSymbol + " -> " + toSymbol
                    + ". Reason: " + ex.getMessage());
        }
    }


    @Override
    public FxDailyDTO getFxDaily(String fromSymbol, String toSymbol) {
        String id = fromSymbol.toUpperCase() + "_" + toSymbol.toUpperCase();
        FxDaily e = repository.findById(id).orElse(null);
        if (e == null) {
            logInfo("No FX data found for " + fromSymbol + " -> " + toSymbol);
            return null;
        }

        FxDailyDTO dto = new FxDailyDTO();
        dto.setId(e.getId());
        dto.setFromSymbol(e.getFromSymbol());
        dto.setToSymbol(e.getToSymbol());

        // Convert arrays to lists for DTO
        dto.setTradeDate(e.getTradeDate() != null ? List.of(e.getTradeDate()) : List.of());
        dto.setOpen(e.getOpen() != null ? List.of(e.getOpen()) : List.of());
        dto.setHigh(e.getHigh() != null ? List.of(e.getHigh()) : List.of());
        dto.setLow(e.getLow() != null ? List.of(e.getLow()) : List.of());
        dto.setClose(e.getClose() != null ? List.of(e.getClose()) : List.of());

        logInfo("FX data retrieved for " + fromSymbol + " -> " + toSymbol);
        return dto;
    }


    /* ===== Logger helper ===== */
    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(logFormatter) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(logFormatter) + "] ERROR: " + msg);
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
}
