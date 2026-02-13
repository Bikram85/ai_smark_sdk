package com.market.alphavantage.service.impl;

import com.market.alphavantage.entity.ETFPrice;
import com.market.alphavantage.entity.StockPrice;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.ETFPriceRepository;
import com.market.alphavantage.repository.StockPriceRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {


    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;
    private final StockPriceRepository stockPriceRepository;
    private final ETFPriceRepository etfPriceRepository;


    @Value("${alphavantage.baseUrl}")
    private String baseUrl;


    @Value("${alphavantage.apiKey}")
    private String apiKey;


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
    public List<ETFPrice> retrieveIndexData(int months) {

        List<ETFPrice> stockPrices = etfPriceRepository.getIndexData();

        if (stockPrices == null || stockPrices.isEmpty()) {
            System.out.println("No index data found");
            return new ArrayList<>();
        }

        LocalDate startDate = months > 0
                ? LocalDate.now().minusMonths(months)
                : null;

        return stockPrices.stream()
                .map(e -> {

                    LocalDate[] allDates =
                            e.getTradeDates() != null ? e.getTradeDates() : new LocalDate[0];

                    // Month filter
                    LocalDate[] filteredDates =
                            startDate != null
                                    ? Arrays.stream(allDates)
                                    .filter(d -> d != null && !d.isBefore(startDate))
                                    .toArray(LocalDate[]::new)
                                    : allDates;

                    Double[] openArr = filterArrayByDates(e.getOpen(), allDates, filteredDates);
                    Double[] highArr = filterArrayByDates(e.getHigh(), allDates, filteredDates);
                    Double[] lowArr = filterArrayByDates(e.getLow(), allDates, filteredDates);
                    Double[] closeArr = filterArrayByDates(e.getClose(), allDates, filteredDates);
                    Double[] volumeArr = filterArrayByDates(e.getVolume(), allDates, filteredDates);

                    openArr = openArr != null ? openArr : new Double[filteredDates.length];
                    highArr = highArr != null ? highArr : new Double[filteredDates.length];
                    lowArr = lowArr != null ? lowArr : new Double[filteredDates.length];
                    closeArr = closeArr != null ? closeArr : new Double[filteredDates.length];
                    volumeArr = volumeArr != null ? volumeArr : new Double[filteredDates.length];

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

                    e.setTradeDates(dates.toArray(new LocalDate[0]));
                    e.setOpen(open.toArray(new Double[0]));
                    e.setHigh(high.toArray(new Double[0]));
                    e.setLow(low.toArray(new Double[0]));
                    e.setClose(close.toArray(new Double[0]));
                    e.setVolume(volume.toArray(new Double[0]));

                    return e;
                })
                .filter(e -> e.getTradeDates().length > 0)
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


    private Long[] filterArrayByDatesLong(
            Long[] data,
            LocalDate[] allDates,
            LocalDate[] filteredDates) {

        if (data == null || allDates == null || filteredDates == null)
            return new Long[0];

        Set<LocalDate> dateSet = new HashSet<>(Arrays.asList(filteredDates));

        return IntStream.range(0, allDates.length)
                .filter(i -> dateSet.contains(allDates[i]) && i < data.length)
                .mapToObj(i -> data[i])
                .toArray(Long[]::new);
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


        Map response = restTemplate.getForObject(url, Map.class);
        Map<String, Map<String, String>> series =
                (Map<String, Map<String, String>>) response.get("Time Series (Daily)");


        if (series == null) return;


        List<LocalDate> dates = new ArrayList<>();
        List<Double> opens = new ArrayList<>();
        List<Double> highs = new ArrayList<>();
        List<Double> lows = new ArrayList<>();
        List<Double> closes = new ArrayList<>();
        List<Double> volumes = new ArrayList<>();

        series.forEach((date, values) -> {

            dates.add(LocalDate.parse(date));
            opens.add(Double.valueOf(values.get("1. open")));
            highs.add(Double.valueOf(values.get("2. high")));
            lows.add(Double.valueOf(values.get("3. low")));
            closes.add(Double.valueOf(values.get("4. close")));
            volumes.add(Double.valueOf(values.get("5. volume")));
        });
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
        }
        else {
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



}
