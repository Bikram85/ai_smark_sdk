package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.GoldSilverHistoryDTO;
import com.market.alphavantage.entity.GoldSilverHistory;
import com.market.alphavantage.repository.GoldSilverHistoryRepository;
import com.market.alphavantage.service.GoldSilverHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class GoldSilverHistoryServiceImpl implements GoldSilverHistoryService {

    private final GoldSilverHistoryRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final String INTERVAL = "daily";
    private static final List<String> GOLDSILVER_SYMBOLS = List.of("GOLD", "SILVER");

    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadHistory() {
        repository.deleteAll();
        GOLDSILVER_SYMBOLS.forEach(symbol -> fetchDetails(symbol, INTERVAL));
    }

    private void fetchDetails(String symbol, String interval) {
        String url = baseUrl
                + "?function=GOLD_SILVER_HISTORY"
                + "&symbol=" + symbol.toUpperCase()
                + "&interval=" + interval.toLowerCase()
                + "&apikey=" + apiKey;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.isEmpty()) {
                logError("No response for " + symbol + " -> " + interval);
                return;
            }

            // Correct key is "data"
            List<Map<String, Object>> series = (List<Map<String, Object>>) response.get("data");
            if (series == null || series.isEmpty()) {
                logError("No series data for " + symbol + " -> " + interval);
                return;
            }

            // Sort by date ascending (oldest first)
            List<Map<String, Object>> sortedSeries = series.stream()
                    .filter(d -> d.get("date") != null)
                    .sorted((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")))
                    .toList();

            List<LocalDate> tradeDates = new ArrayList<>();
            List<Double> opens = new ArrayList<>();
            List<Double> highs = new ArrayList<>();
            List<Double> lows = new ArrayList<>();
            List<Double> closes = new ArrayList<>();
            List<Long> volumes = new ArrayList<>();

            for (Map<String, Object> values : sortedSeries) {
                String dateStr = (String) values.get("date");
                tradeDates.add(parseDate(dateStr));

                // Some APIs may not provide open/high/low/volume, only "price"
                Double price = parseDouble((String) values.get("price"));
                opens.add(price);
                highs.add(price);
                lows.add(price);
                closes.add(price);

                // Volume may not exist, so set 0
                Long vol = parseLong(values.get("volume"));
                volumes.add(vol);
            }

            // Save to entity
            GoldSilverHistory entity = new GoldSilverHistory();
            String id = symbol.toUpperCase() + "_" + interval.toLowerCase();
            entity.setId(id);
            entity.setSymbol(symbol.toUpperCase());
            entity.setInterval(interval.toLowerCase());

            entity.setTradeDate(tradeDates.toArray(new LocalDate[0]));
            entity.setOpen(opens.toArray(new Double[0]));
            entity.setHigh(highs.toArray(new Double[0]));
            entity.setLow(lows.toArray(new Double[0]));
            entity.setClose(closes.toArray(new Double[0]));
            entity.setVolume(volumes.toArray(new Long[0]));

            repository.save(entity);
            logInfo("Saved history for " + id);

        } catch (Exception ex) {
            logError("Failed to fetch history for " + symbol + " -> " + interval
                    + ". Reason: " + ex.getMessage());
        }
    }

    /* ===== Helper parsers ===== */
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

    private Long parseLong(Object val) {
        try {
            if (val == null) return 0L;
            if (val instanceof String) return Long.parseLong((String) val);
            if (val instanceof Number) return ((Number) val).longValue();
            return 0L;
        } catch (Exception ex) {
            return 0L;
        }
    }


    @Override
    public List<GoldSilverHistoryDTO> getHistoryByMonths(int month) {
        List<GoldSilverHistory> histories = repository.findAll();

        if (histories == null || histories.isEmpty()) {
            logInfo("No gold/silver history found");
            return new ArrayList<>();
        }

        // If month > 0, calculate startDate; else null means include all dates
        LocalDate startDate = month > 0 ? LocalDate.now().minusMonths(month) : null;

        return histories.stream()
                .map(e -> {
                    // Filter dates based on startDate if month > 0
                    LocalDate[] filteredDates = e.getTradeDate() != null
                            ? (startDate != null
                            ? Arrays.stream(e.getTradeDate())
                            .filter(d -> !d.isBefore(startDate))
                            .toArray(LocalDate[]::new)
                            : e.getTradeDate())
                            : new LocalDate[0];

                    // Safely filter arrays based on filteredDates
                    Double[] openArray = filterArrayByDates(e.getOpen(), e.getTradeDate(), filteredDates);
                    Double[] highArray = filterArrayByDates(e.getHigh(), e.getTradeDate(), filteredDates);
                    Double[] lowArray = filterArrayByDates(e.getLow(), e.getTradeDate(), filteredDates);
                    Double[] closeArray = filterArrayByDates(e.getClose(), e.getTradeDate(), filteredDates);
                    Long[] volumeArray = filterArrayByDatesLong(e.getVolume(), e.getTradeDate(), filteredDates);

                    // Default arrays to filteredDates length if null
                    openArray = openArray != null ? openArray : new Double[filteredDates.length];
                    highArray = highArray != null ? highArray : new Double[filteredDates.length];
                    lowArray = lowArray != null ? lowArray : new Double[filteredDates.length];
                    closeArray = closeArray != null ? closeArray : new Double[filteredDates.length];
                    volumeArray = volumeArray != null ? volumeArray : new Long[filteredDates.length];

                    // Collect only rows where close != 0
                    List<LocalDate> finalDates = new ArrayList<>();
                    List<Double> finalOpen = new ArrayList<>();
                    List<Double> finalHigh = new ArrayList<>();
                    List<Double> finalLow = new ArrayList<>();
                    List<Double> finalClose = new ArrayList<>();
                    List<Long> finalVolume = new ArrayList<>();

                    for (int i = 0; i < filteredDates.length; i++) {
                        if (filteredDates[i] != null &&
                                closeArray.length > i &&
                                closeArray[i] != null &&
                                closeArray[i] != 0) {

                            finalDates.add(filteredDates[i]);
                            finalOpen.add(i < openArray.length ? openArray[i] : null);
                            finalHigh.add(i < highArray.length ? highArray[i] : null);
                            finalLow.add(i < lowArray.length ? lowArray[i] : null);
                            finalClose.add(closeArray[i]);
                            finalVolume.add(i < volumeArray.length ? volumeArray[i] : null);
                        }
                    }

                    // Create DTO
                    GoldSilverHistoryDTO dto = new GoldSilverHistoryDTO();
                    dto.setId(e.getId());
                    dto.setSymbol(e.getSymbol());
                    dto.setInterval(e.getInterval());
                    dto.setTradeDate(finalDates);
                    dto.setOpen(finalOpen);
                    dto.setHigh(finalHigh);
                    dto.setLow(finalLow);
                    dto.setClose(finalClose);
                    dto.setVolume(finalVolume);

                    return dto;
                })
                .filter(dto -> !dto.getTradeDate().isEmpty()) // remove DTOs with no valid data
                .collect(Collectors.toList());
    }



    @Override
    public GoldSilverHistoryDTO getHistory(String symbol, String interval) {
        String id = symbol.toUpperCase() + "_" + interval.toLowerCase();
        GoldSilverHistory e = repository.findById(id).orElse(null);
        if (e == null) {
            logInfo("No history found for " + symbol + " -> " + interval);
            return null;
        }

        GoldSilverHistoryDTO dto = new GoldSilverHistoryDTO();
        dto.setId(e.getId());
        dto.setSymbol(e.getSymbol());
        dto.setInterval(e.getInterval());

        // Convert arrays to lists for DTO
        dto.setTradeDate(e.getTradeDate() != null ? List.of(e.getTradeDate()) : List.of());
        dto.setOpen(e.getOpen() != null ? List.of(e.getOpen()) : List.of());
        dto.setHigh(e.getHigh() != null ? List.of(e.getHigh()) : List.of());
        dto.setLow(e.getLow() != null ? List.of(e.getLow()) : List.of());
        dto.setClose(e.getClose() != null ? List.of(e.getClose()) : List.of());
        dto.setVolume(e.getVolume() != null ? List.of(e.getVolume()) : List.of());

        logInfo("Retrieved history for " + symbol + " -> " + interval);
        return dto;
    }


    // Helper methods
    private Double[] filterArrayByDates(Double[] data, LocalDate[] allDates, LocalDate[] filteredDates) {
        if (data == null || allDates == null || filteredDates == null) return new Double[0];
        return IntStream.range(0, allDates.length)
                .filter(i -> Arrays.asList(filteredDates).contains(allDates[i]))
                .mapToDouble(i -> data[i])
                .boxed()
                .toArray(Double[]::new);
    }

    private Long[] filterArrayByDatesLong(Long[] data, LocalDate[] allDates, LocalDate[] filteredDates) {
        if (data == null || allDates == null || filteredDates == null) return new Long[0];
        return IntStream.range(0, allDates.length)
                .filter(i -> Arrays.asList(filteredDates).contains(allDates[i]))
                .mapToObj(i -> data[i])
                .toArray(Long[]::new);
    }


    private Long parseLong(String val) {
        try {
            return val == null || val.isBlank() ? 0L : Long.valueOf(val);
        } catch (Exception ex) {
            return 0L;
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
