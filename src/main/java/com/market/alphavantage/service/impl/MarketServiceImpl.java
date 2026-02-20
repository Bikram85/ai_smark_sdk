package com.market.alphavantage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.alphavantage.dto.ETFPriceDTO;
import com.market.alphavantage.entity.*;
import com.market.alphavantage.repository.AnalyticsRepository;
import com.market.alphavantage.repository.ETFPriceRepository;
import com.market.alphavantage.repository.StockPriceRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {


    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;
    private final StockPriceRepository stockPriceRepository;
    private final ETFPriceRepository etfPriceRepository;
    private final AnalyticsRepository analyticsRepository;


    @Value("${alphavantage.baseUrl}")
    private String baseUrl;


    @Value("${alphavantage.apiKey}")
    private String apiKey;


    private final ObjectMapper mapper = new ObjectMapper();

    // API 1: Listing status
    @Override
    public void loadListingStatus() {
        String url = baseUrl + "?function=LISTING_STATUS&&entitlement=delayed&apikey=" + apiKey;
        String csv = restTemplate.getForObject(url, String.class);


        Arrays.stream(csv.split("\n"))
                .skip(1)
                .map(line -> line.split(","))
                .forEach(cols -> {
                    Symbol s = new Symbol();
                    s.setSymbol(cols[0]);
                    s.setName(cols[1]);
                    s.setExchange(cols[2]);
                    s.setAssetType(cols[3]);
                    symbolRepo.save(s);
                });
    }


    // API 2: Daily prices
    @Override
    public void loadDailyPrices() {

        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");
        List<Symbol> etfs = symbolRepo.findByAssetType("ETF");

        int total = stocks.size() + etfs.size();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol -> {
            processSymbol(symbol.getSymbol(), "Stock",
                    processed, success, failed, total);
        });

        etfs.forEach(symbol -> {
            processSymbol(symbol.getSymbol(), "ETF",
                    processed, success, failed, total);
        });

        System.out.println("\n===== SUMMARY =====");
        System.out.println("Total symbols : " + total);
        System.out.println("Success       : " + success.get());
        System.out.println("Failed        : " + failed.get());
    }

    @Override
    public void fetchBulkIntraday() {

        List<String> highVolumeSymbols =
                analyticsRepository.findSymbolsWithAvgVolumeGreaterThan(500000.0);

        int total = highVolumeSymbols.size();
        int processed = 0;
        int failed = 0;

        for (String symbol : highVolumeSymbols) {
            boolean success = fetchAndUpdateIntraday(symbol);
            if (success) {
                processed++;
            } else {
                failed++;
            }
        }

        System.out.println("Total symbols today: " + total);
        System.out.println("Successfully processed: " + processed);
        System.out.println("Failed: " + failed);
    }



    @Override
    public List<ETFPriceDTO> retrieveIndexData(int months) {

        List<ETFPrice> stockPrices = etfPriceRepository.getIndexData();

        if (stockPrices == null || stockPrices.isEmpty()) {
            System.out.println("No index data found");
            return new ArrayList<>();
        }

        LocalDate startDate = months > 0 ? LocalDate.now().minusMonths(months) : null;

        return stockPrices.stream()
                .map(e -> {
                    LocalDate[] allDates = e.getTradeDates() != null ? e.getTradeDates() : new LocalDate[0];

                    // Filter dates by months
                    LocalDate[] filteredDates = startDate != null
                            ? Arrays.stream(allDates)
                            .filter(d -> d != null && !d.isBefore(startDate))
                            .toArray(LocalDate[]::new)
                            : allDates;

                    Double[] openArr = filterArrayByDates(e.getOpen(), allDates, filteredDates);
                    Double[] highArr = filterArrayByDates(e.getHigh(), allDates, filteredDates);
                    Double[] lowArr = filterArrayByDates(e.getLow(), allDates, filteredDates);
                    Double[] closeArr = filterArrayByDates(e.getClose(), allDates, filteredDates);
                    Double[] volumeArr = filterArrayByDates(e.getVolume(), allDates, filteredDates);

                    List<LocalDate> dates = new ArrayList<>();
                    List<Double> open = new ArrayList<>();
                    List<Double> high = new ArrayList<>();
                    List<Double> low = new ArrayList<>();
                    List<Double> close = new ArrayList<>();
                    List<Double> volume = new ArrayList<>();

                    for (int i = 0; i < filteredDates.length; i++) {
                        if (filteredDates[i] != null
                                && i < closeArr.length
                                && closeArr[i] != null
                                && closeArr[i] != 0) {

                            dates.add(filteredDates[i]);
                            open.add(i < openArr.length ? openArr[i] : null);
                            high.add(i < highArr.length ? highArr[i] : null);
                            low.add(i < lowArr.length ? lowArr[i] : null);
                            close.add(closeArr[i]);
                            volume.add(i < volumeArr.length ? volumeArr[i] : null);
                        }
                    }

                    return new ETFPriceDTO(
                            e.getSymbol(),
                            dates,
                            open,
                            high,low,
                            close,
                            volume
                    );
                })
                .filter(dto -> dto.getTradeDates() != null && !dto.getTradeDates().isEmpty())
                .toList();
    }


    private Double[] filterArrayByDates(
            Double[] data,
            LocalDate[] allDates,
            LocalDate[] filteredDates) {

        if (data == null || allDates == null || filteredDates == null)
            return new Double[0];

        Set<LocalDate> dateSet = new HashSet<>(Arrays.asList(filteredDates));

        return IntStream.range(0, allDates.length)
                .filter(i -> dateSet.contains(allDates[i]) && i < data.length)
                .mapToObj(i -> data[i])
                .toArray(Double[]::new);
    }







    private void processSymbol(String symbol,
                               String type,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {

        int current = processed.incrementAndGet();

        try {
            fetchDaily(symbol, type);
            success.incrementAndGet();

            System.out.println("Processed "
                    + current + "/" + total
                    + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();

            System.err.println("Processed "
                    + current + "/" + total
                    + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }





    private void fetchDaily(String symbol, String type) {
        String url = baseUrl
                + "?function=TIME_SERIES_DAILY"
                + "&entitlement=delayed"
                + "&symbol=" + symbol
                + "&outputsize=full"
                + "&apikey=" + apiKey;

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map response = restTemplate.getForObject(url, Map.class);
        Map<String, Map<String, String>> series =
                (Map<String, Map<String, String>>) response.get("Time Series (Daily)");

        if (series == null) {
            System.out.println("Series is null: " + symbol + " " + type);
            return;
        }

        // Convert the Map to a sorted list of entries by date (ascending)
        List<Map.Entry<String, Map<String, String>>> sortedEntries = new ArrayList<>(series.entrySet());
        sortedEntries.sort(Comparator.comparing(e -> LocalDate.parse(e.getKey())));

        // Prepare lists
        List<LocalDate> dates = new ArrayList<>();
        List<Double> opens = new ArrayList<>();
        List<Double> highs = new ArrayList<>();
        List<Double> lows = new ArrayList<>();
        List<Double> closes = new ArrayList<>();
        List<Double> volumes = new ArrayList<>();

        // Fill the lists in ascending order
        for (Map.Entry<String, Map<String, String>> entry : sortedEntries) {
            LocalDate date = LocalDate.parse(entry.getKey());
            Map<String, String> values = entry.getValue();

            dates.add(date);
            opens.add(Double.valueOf(values.get("1. open")));
            highs.add(Double.valueOf(values.get("2. high")));
            lows.add(Double.valueOf(values.get("3. low")));
            closes.add(Double.valueOf(values.get("4. close")));
            volumes.add(Double.valueOf(values.get("5. volume")));
        }

        if (type.contains("Stock")) {
            StockPrice dp = new StockPrice();
            dp.setSymbol(symbol);
            dp.setTradeDates(dates.toArray(new LocalDate[0]));
            dp.setOpen(opens.toArray(new Double[0]));
            dp.setHigh(highs.toArray(new Double[0]));
            dp.setLow(lows.toArray(new Double[0]));
            dp.setClose(closes.toArray(new Double[0]));
            dp.setVolume(volumes.toArray(new Double[0]));

            stockPriceRepository.save(dp);
        } else {
            ETFPrice dp = new ETFPrice();
            dp.setSymbol(symbol);
            dp.setTradeDates(dates.toArray(new LocalDate[0]));
            dp.setOpen(opens.toArray(new Double[0]));
            dp.setHigh(highs.toArray(new Double[0]));
            dp.setLow(lows.toArray(new Double[0]));
            dp.setClose(closes.toArray(new Double[0]));
            dp.setVolume(volumes.toArray(new Double[0]));

            etfPriceRepository.save(dp);
        }
    }

    @Transactional
    public boolean fetchAndUpdateIntraday(String symbol) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {

            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY"
                    + "&symbol=" + symbol
                    + "&interval=5min"
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);

            // API limit check
            if (root.has("Note") || root.has("Error Message")) {
                System.out.println("API limit hit or error for " + symbol);
                return false;
            }

            JsonNode meta = root.get("Meta Data");
            JsonNode series = root.get("Time Series (5min)");
            if (meta == null || series == null) return false;

            String latestTimestamp = meta.get("3. Last Refreshed").asText();
            JsonNode candle = series.get(latestTimestamp);
            if (candle == null) return false;

            double open = candle.get("1. open").asDouble();
            double high = candle.get("2. high").asDouble();
            double low = candle.get("3. low").asDouble();
            double close = candle.get("4. close").asDouble();
            double volume = candle.get("5. volume").asDouble();

            LocalDate todayDate = LocalDate.now();

            Optional<StockPrice> optional = stockPriceRepository.findById(symbol);
            if (optional.isEmpty()) return false;

            StockPrice entity = optional.get();

            List<LocalDate> dates = new ArrayList<>(Arrays.asList(entity.getTradeDates()));
            List<Double> opens = new ArrayList<>(Arrays.asList(entity.getOpen()));
            List<Double> highs = new ArrayList<>(Arrays.asList(entity.getHigh()));
            List<Double> lows = new ArrayList<>(Arrays.asList(entity.getLow()));
            List<Double> closes = new ArrayList<>(Arrays.asList(entity.getClose()));
            List<Double> volumes = new ArrayList<>(Arrays.asList(entity.getVolume()));

            int lastIndex = dates.size() - 1;

            if (!dates.isEmpty() && dates.get(lastIndex).equals(todayDate)) {

                highs.set(lastIndex, Math.max(highs.get(lastIndex), high));
                lows.set(lastIndex, Math.min(lows.get(lastIndex), low));
                closes.set(lastIndex, close);
                volumes.set(lastIndex, volume);

            } else {
                dates.add(todayDate);
                opens.add(open);
                highs.add(high);
                lows.add(low);
                closes.add(close);
                volumes.add(volume);
            }

            entity.setTradeDates(dates.toArray(new LocalDate[0]));
            entity.setOpen(opens.toArray(new Double[0]));
            entity.setHigh(highs.toArray(new Double[0]));
            entity.setLow(lows.toArray(new Double[0]));
            entity.setClose(closes.toArray(new Double[0]));
            entity.setVolume(volumes.toArray(new Double[0]));

            stockPriceRepository.save(entity);

            Analytics analytics = analyticsRepository
                    .findById(symbol)
                    .orElseGet(() -> {
                        Analytics a = new Analytics();
                        a.setSymbol(symbol);
                        return a;
                    });

            analytics.setClosePrice(close);
            analytics.setVolume(volume);
            analytics.setUpdatedAt(LocalDateTime.now());

            analyticsRepository.save(analytics);

            return true;

        } catch (Exception e) {
            System.out.println("Intraday update failed for " + symbol + ": " + e.getMessage());
            return false;
        }
    }










}
