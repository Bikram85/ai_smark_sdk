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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        repository.deleteAll();
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
    public List<CommodityDTO> getCommodity() {
        List<Commodity> commodities = repository.findAll();

        if (commodities == null || commodities.isEmpty()) {
            logInfo("No commodity data found");
            return new ArrayList<>();
        }

        return commodities.stream()
                .map(e -> {
                    LocalDate[] datesArray = e.getTradeDate() != null ? e.getTradeDate() : new LocalDate[0];
                    Double[] openArray = e.getOpen() != null ? e.getOpen() : new Double[datesArray.length];
                    Double[] highArray = e.getHigh() != null ? e.getHigh() : new Double[datesArray.length];
                    Double[] lowArray = e.getLow() != null ? e.getLow() : new Double[datesArray.length];
                    Double[] closeArray = e.getClose() != null ? e.getClose() : new Double[datesArray.length];
                    Long[] volumeArray = e.getVolume() != null ? e.getVolume() : new Long[datesArray.length];

                    List<LocalDate> tradeDate = new ArrayList<>();
                    List<Double> open = new ArrayList<>();
                    List<Double> high = new ArrayList<>();
                    List<Double> low = new ArrayList<>();
                    List<Double> close = new ArrayList<>();
                    List<Long> volume = new ArrayList<>();

                    // Loop over datesArray and include only rows with non-zero close
                    for (int i = 0; i < datesArray.length; i++) {
                        if (datesArray[i] != null &&
                                closeArray[i] != null &&
                                closeArray[i] != 0
                        ) {
                            tradeDate.add(datesArray[i]);
                            open.add(i < openArray.length ? openArray[i] : null);
                            high.add(i < highArray.length ? highArray[i] : null);
                            low.add(i < lowArray.length ? lowArray[i] : null);
                            close.add(closeArray[i]);
                            volume.add(i < volumeArray.length ? volumeArray[i] : null);
                        }
                    }

                    logInfo("Retrieved commodity data for " + e.getFunction() + " -> " + e.getInterval());

                    return new CommodityDTO(
                            e.getId(),
                            e.getFunction(),
                            e.getInterval(),
                            tradeDate,
                            open,
                            high,
                            low,
                            close,
                            volume
                    );
                })
                .filter(dto -> !dto.getTradeDate().isEmpty()) // remove DTOs with no valid data
                .collect(Collectors.toList());
    }





    @Override
    public List<CommodityDTO> getCommodityByMonths(int months) {

        // Only calculate startDate if months > 0
        LocalDate startDate = months > 0 ? LocalDate.now().minusMonths(months) : null;

        return repository.findAll().stream()
                .map(e -> {
                    // Filter dates by startDate if applicable
                    LocalDate[] filteredDates = e.getTradeDate() != null
                            ? (startDate != null
                            ? Arrays.stream(e.getTradeDate())
                            .filter(d -> !d.isBefore(startDate))
                            .toArray(LocalDate[]::new)
                            : e.getTradeDate()) // include all dates if startDate is null
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

                    return new CommodityDTO(
                            e.getId(),
                            e.getFunction(),
                            e.getInterval(),
                            finalDates,
                            finalOpen,
                            finalHigh,
                            finalLow,
                            finalClose,
                            finalVolume
                    );
                })
                .filter(dto -> !dto.getTradeDate().isEmpty()) // remove empty DTOs
                .collect(Collectors.toList());
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
