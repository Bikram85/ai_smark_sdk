package com.market.alphavantage.service.impl;

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
import java.util.stream.IntStream;

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
        CRYPTO_SYMBOLS.forEach(symbol -> fetchDetails(symbol, BASE_CURRENCY));
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

    @Override
    public List<DigitalCurrencyDailyDTO> getDigitalCurrencyByMonths(int months) {
        LocalDate startDate = LocalDate.now().minusMonths(months);

        return repository.findAll().stream()
                .map(e -> {
                    // filter arrays
                    LocalDate[] tradeDates = e.getTradeDate() != null
                            ? Arrays.stream(e.getTradeDate())
                            .filter(d -> !d.isBefore(startDate))
                            .toArray(LocalDate[]::new)
                            : new LocalDate[0];

                    Double[] open = e.getOpen() != null
                            ? IntStream.range(0, e.getOpen().length)
                            .filter(i -> tradeDates.length > 0 && Arrays.asList(e.getTradeDate()).subList(e.getTradeDate().length - tradeDates.length, e.getTradeDate().length).contains(e.getTradeDate()[i]))
                            .mapToDouble(i -> e.getOpen()[i])
                            .boxed()
                            .toArray(Double[]::new)
                            : new Double[0];

                    Double[] high = e.getHigh() != null
                            ? IntStream.range(0, e.getHigh().length)
                            .filter(i -> tradeDates.length > 0 && Arrays.asList(e.getTradeDate()).subList(e.getTradeDate().length - tradeDates.length, e.getTradeDate().length).contains(e.getTradeDate()[i]))
                            .mapToDouble(i -> e.getHigh()[i])
                            .boxed()
                            .toArray(Double[]::new)
                            : new Double[0];

                    Double[] low = e.getLow() != null
                            ? IntStream.range(0, e.getLow().length)
                            .filter(i -> tradeDates.length > 0 && Arrays.asList(e.getTradeDate()).subList(e.getTradeDate().length - tradeDates.length, e.getTradeDate().length).contains(e.getTradeDate()[i]))
                            .mapToDouble(i -> e.getLow()[i])
                            .boxed()
                            .toArray(Double[]::new)
                            : new Double[0];

                    Double[] close = e.getClose() != null
                            ? IntStream.range(0, e.getClose().length)
                            .filter(i -> tradeDates.length > 0 && Arrays.asList(e.getTradeDate()).subList(e.getTradeDate().length - tradeDates.length, e.getTradeDate().length).contains(e.getTradeDate()[i]))
                            .mapToDouble(i -> e.getClose()[i])
                            .boxed()
                            .toArray(Double[]::new)
                            : new Double[0];

                    Double[] volume = e.getVolume() != null
                            ? IntStream.range(0, e.getVolume().length)
                            .filter(i -> tradeDates.length > 0 && Arrays.asList(e.getTradeDate()).subList(e.getTradeDate().length - tradeDates.length, e.getTradeDate().length).contains(e.getTradeDate()[i]))
                            .mapToDouble(i -> e.getVolume()[i])
                            .boxed()
                            .toArray(Double[]::new)
                            : new Double[0];

                    Double[] marketCap = e.getMarketCap() != null
                            ? IntStream.range(0, e.getMarketCap().length)
                            .filter(i -> tradeDates.length > 0 && Arrays.asList(e.getTradeDate()).subList(e.getTradeDate().length - tradeDates.length, e.getTradeDate().length).contains(e.getTradeDate()[i]))
                            .mapToDouble(i -> e.getMarketCap()[i])
                            .boxed()
                            .toArray(Double[]::new)
                            : new Double[0];

                    return new DigitalCurrencyDailyDTO(
                            e.getId(),
                            e.getSymbol(),
                            e.getMarket(),
                            tradeDates != null ? Arrays.asList(tradeDates) : new ArrayList<>(),
                            open != null ? Arrays.asList(open) : new ArrayList<>(),
                            high != null ? Arrays.asList(high) : new ArrayList<>(),
                            low != null ? Arrays.asList(low) : new ArrayList<>(),
                            close != null ? Arrays.asList(close) : new ArrayList<>(),
                            volume != null ? Arrays.asList(volume) : new ArrayList<>(),
                            marketCap != null ? Arrays.asList(marketCap) : new ArrayList<>()
                    );
                })
                .filter(dto -> !dto.getTradeDate().isEmpty()) // remove empty ones
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
