package com.market.alphavantage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        repository.deleteAll();
        FX_SYMBOLS.forEach(symbol -> fetchDetails(symbol, BASE_CURRENCY));
    }

    @Override
    public void loadFxIntraday() {
        FX_SYMBOLS.forEach(symbol -> fetchAndUpdateFxIntraday(symbol, BASE_CURRENCY));
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

    public void fetchAndUpdateFxIntraday(String fromSymbol, String toSymbol) {

        try {

            String url = baseUrl
                    + "?function=FX_INTRADAY"
                    + "&from_symbol=" + fromSymbol.toUpperCase()
                    + "&to_symbol=" + toSymbol.toUpperCase()
                    + "&interval=5min"
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = new ObjectMapper().readTree(response);

            // 🔥 API limit protection
            if (root.has("Note") || root.has("Error Message")) {
                logError("API limit hit for FX intraday " + fromSymbol);
                return;
            }

            JsonNode meta = root.get("Meta Data");
            JsonNode series = root.get("Time Series FX (5min)");

            if (meta == null || series == null) return;

            String latestTimestamp = meta.get("4. Last Refreshed").asText();
            JsonNode candle = series.get(latestTimestamp);
            if (candle == null) return;

            double open = parseDouble(candle.get("1. open").asText());
            double high = parseDouble(candle.get("2. high").asText());
            double low = parseDouble(candle.get("3. low").asText());
            double close = parseDouble(candle.get("4. close").asText());

            LocalDate today = LocalDate.parse(latestTimestamp.substring(0, 10));

            String id = fromSymbol.toUpperCase() + "_" + toSymbol.toUpperCase();
            Optional<FxDaily> optional = repository.findById(id);
            if (optional.isEmpty()) return;

            FxDaily entity = optional.get();

            List<LocalDate> dates = new ArrayList<>(Arrays.asList(entity.getTradeDate()));
            List<Double> opens = new ArrayList<>(Arrays.asList(entity.getOpen()));
            List<Double> highs = new ArrayList<>(Arrays.asList(entity.getHigh()));
            List<Double> lows = new ArrayList<>(Arrays.asList(entity.getLow()));
            List<Double> closes = new ArrayList<>(Arrays.asList(entity.getClose()));

            int lastIndex = dates.size() - 1;

            // ✅ If today exists → UPDATE
            if (!dates.isEmpty() && dates.get(lastIndex).equals(today)) {

                highs.set(lastIndex, Math.max(highs.get(lastIndex), high));
                lows.set(lastIndex, Math.min(lows.get(lastIndex), low));
                closes.set(lastIndex, close);

            }
            // ✅ If new day → append
            else {

                dates.add(today);
                opens.add(open);
                highs.add(high);
                lows.add(low);
                closes.add(close);
            }

            entity.setTradeDate(dates.toArray(new LocalDate[0]));
            entity.setOpen(opens.toArray(new Double[0]));
            entity.setHigh(highs.toArray(new Double[0]));
            entity.setLow(lows.toArray(new Double[0]));
            entity.setClose(closes.toArray(new Double[0]));

            repository.save(entity);

            logInfo("Updated FX intraday for " + id);

        } catch (Exception ex) {
            logError("Failed FX intraday for " + fromSymbol + " -> " + toSymbol
                    + ". Reason: " + ex.getMessage());
        }
    }



    @Override
    public List<FxDailyDTO> getFxDailyByMonths() {
        LocalDate startDate =  null;

        return repository.findAll().stream()
                .map(fx -> {
                    // Filter dates by startDate if months > 0
                    LocalDate[] filteredDates = fx.getTradeDate() != null
                            ? (startDate != null
                            ? Arrays.stream(fx.getTradeDate())
                            .filter(d -> !d.isBefore(startDate))
                            .toArray(LocalDate[]::new)
                            : fx.getTradeDate())
                            : new LocalDate[0];

                    // Safely filter arrays based on filteredDates
                    Double[] openArray = filterArrayByDates(fx.getOpen(), fx.getTradeDate(), filteredDates);
                    Double[] highArray = filterArrayByDates(fx.getHigh(), fx.getTradeDate(), filteredDates);
                    Double[] lowArray = filterArrayByDates(fx.getLow(), fx.getTradeDate(), filteredDates);
                    Double[] closeArray = filterArrayByDates(fx.getClose(), fx.getTradeDate(), filteredDates);

                    openArray = openArray != null ? openArray : new Double[filteredDates.length];
                    highArray = highArray != null ? highArray : new Double[filteredDates.length];
                    lowArray = lowArray != null ? lowArray : new Double[filteredDates.length];
                    closeArray = closeArray != null ? closeArray : new Double[filteredDates.length];

                    // Collect only rows where close != 0
                    List<LocalDate> finalDates = new ArrayList<>();
                    List<Double> finalOpen = new ArrayList<>();
                    List<Double> finalHigh = new ArrayList<>();
                    List<Double> finalLow = new ArrayList<>();
                    List<Double> finalClose = new ArrayList<>();

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
                        }
                    }

                    FxDailyDTO dto = new FxDailyDTO();
                    dto.setId(fx.getId());
                    dto.setFromSymbol(fx.getFromSymbol());
                    dto.setToSymbol(fx.getToSymbol());
                    dto.setTradeDate(finalDates);
                    dto.setOpen(finalOpen);
                    dto.setHigh(finalHigh);
                    dto.setLow(finalLow);
                    dto.setClose(finalClose);

                    return dto;
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
