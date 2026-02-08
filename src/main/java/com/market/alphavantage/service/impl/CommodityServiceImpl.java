package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.CommodityDTO;
import com.market.alphavantage.entity.Commodity;
import com.market.alphavantage.repository.CommodityRepository;
import com.market.alphavantage.service.CommodityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommodityServiceImpl implements CommodityService {

    private final CommodityRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final String OCURRENCE = "daily";

    private static final List<String> COMMODITIES_SYMBOLS = List.of(
            "WTI",
            "BRENT",
            "NATURAL_GAS",
            "COPPER",
            "ALUMINUM",
            "WHEAT",
            "CORN",
            "COTTON",
            "SUGAR",
            "COFFEE",
            "ALL_COMMODITIES"
    );

    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadCommodity() {
        COMMODITIES_SYMBOLS.forEach(symbol -> fetchDetails(symbol, OCURRENCE));
    }

    private void fetchDetails(String function, String interval) {
        String url = baseUrl
                + "?function=" + function.toUpperCase()
                + "&interval=" + interval.toLowerCase()
                + "&apikey=" + apiKey;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.isEmpty()) {
                logError("No response for " + function + " -> " + interval);
                return;
            }

            // NEW: API returns 'data' array
            List<Map<String, String>> series = (List<Map<String, String>>) response.get("data");
            if (series == null || series.isEmpty()) {
                logError("No series data for " + function + " -> " + interval);
                return;
            }

            // Sort oldest to newest
            series.sort(Comparator.comparing(m -> parseDate(m.get("date"))));

            Commodity entity = new Commodity();
            String id = function.toUpperCase() + "_" + interval.toLowerCase();
            entity.setId(id);
            entity.setFunction(function.toUpperCase());
            entity.setInterval(interval.toLowerCase());

            List<LocalDate> tradeDates = new ArrayList<>();
            List<Double> values = new ArrayList<>();

            for (Map<String, String> point : series) {
                tradeDates.add(parseDate(point.get("date")));
                values.add(parseDouble(point.get("value")));
            }

            entity.setTradeDate(tradeDates.toArray(new LocalDate[0]));
            entity.setClose(values.toArray(new Double[0])); // store value in 'close', others left null

            repository.save(entity);
            logInfo("Saved commodity data for " + id);

        } catch (Exception ex) {
            logError("Failed to fetch commodity " + function + " -> " + interval
                    + ". Reason: " + ex.getMessage());
        }
    }

    @Override
    public CommodityDTO getCommodity(String function, String interval) {
        String id = function.toUpperCase() + "_" + interval.toLowerCase();
        Commodity e = repository.findById(id).orElse(null);
        if (e == null) {
            logInfo("No commodity data found for " + function + " -> " + interval);
            return null;
        }

        // Convert arrays to lists for DTO
        List<LocalDate> tradeDate = e.getTradeDate() != null ? List.of(e.getTradeDate()) : List.of();
        List<Double> close = e.getClose() != null ? List.of(e.getClose()) : List.of();

        logInfo("Retrieved commodity data for " + function + " -> " + interval);

        return new CommodityDTO(
                e.getId(),
                e.getFunction(),
                e.getInterval(),
                tradeDate,
                null, // open
                null, // high
                null, // low
                close,
                null  // volume
        );
    }

    /* ===== Helper Methods ===== */
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

    /* ===== Logging ===== */
    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(logFormatter) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(logFormatter) + "] ERROR: " + msg);
    }
}
