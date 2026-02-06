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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        symbolRepo.findByAssetType("Stock").stream()
                .forEach(symbol -> fetchDaily(symbol.getSymbol(), "Stock"));

        symbolRepo.findByAssetType("ETF").stream()
                .forEach(symbol -> fetchDaily(symbol.getSymbol(), "ETF"));
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
        List<Long> volumes = new ArrayList<>();

        series.forEach((date, values) -> {

            dates.add(LocalDate.parse(date));
            opens.add(Double.valueOf(values.get("1. open")));
            highs.add(Double.valueOf(values.get("2. high")));
            lows.add(Double.valueOf(values.get("3. low")));
            closes.add(Double.valueOf(values.get("4. close")));
            volumes.add(Long.valueOf(values.get("5. volume")));
        });
        if (type.contains("Stock")) {
        StockPrice dp = new StockPrice();
        dp.setSymbol(symbol);
        dp.setTradeDates(dates.toArray(new LocalDate[0]));
        dp.setOpen(opens.toArray(new Double[0]));
        dp.setHigh(highs.toArray(new Double[0]));
        dp.setLow(lows.toArray(new Double[0]));
        dp.setClose(closes.toArray(new Double[0]));
        dp.setVolume(volumes.toArray(new Long[0]));

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
            dp.setVolume(volumes.toArray(new Long[0]));
            etfPriceRepository.save(dp);
        }
    }



}
