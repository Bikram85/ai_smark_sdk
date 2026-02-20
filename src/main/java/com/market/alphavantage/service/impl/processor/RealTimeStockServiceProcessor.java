package com.market.alphavantage.service.impl.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.alphavantage.entity.Analytics;
import com.market.alphavantage.entity.StockPrice;
import com.market.alphavantage.repository.AnalyticsRepository;
import com.market.alphavantage.repository.ETFPriceRepository;
import com.market.alphavantage.repository.StockPriceRepository;
import com.market.alphavantage.repository.SymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RealTimeStockServiceProcessor {
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


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean fetchAndUpdateIntraday(String symbol) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE"
                    + "&symbol=" + symbol
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);

            // API limit or error check
            if (root.has("Note") || root.has("Error Message")) {
                System.out.println("API limit hit or error for " + symbol);
                return false;
            }

            JsonNode quote = root.get("Global Quote");
            if (quote == null || quote.isEmpty()) return false;

            double close = quote.get("05. price").asDouble();
            double volume = quote.get("06. volume").asDouble();
            double previousClose = quote.get("08. previous close").asDouble();
            double changeAmount = quote.get("09. change").asDouble();
            String changePercentStr = quote.get("10. change percent").asText().replace("%", "");
            double changePercent = Double.parseDouble(changePercentStr);

            LocalDate latestDate = LocalDate.parse(quote.get("07. latest trading day").asText());

            // Update StockPrice entity if needed
            Optional<StockPrice> optional = stockPriceRepository.findById(symbol);
            if (optional.isPresent()) {
                StockPrice entity = optional.get();

                List<LocalDate> dates = new ArrayList<>(Arrays.asList(entity.getTradeDates()));
                List<Double> closes = new ArrayList<>(Arrays.asList(entity.getClose()));
                List<Double> volumes = new ArrayList<>(Arrays.asList(entity.getVolume()));

                int lastIndex = dates.size() - 1;

                if (!dates.isEmpty() && dates.get(lastIndex).equals(latestDate)) {
                    closes.set(lastIndex, close);
                    volumes.set(lastIndex, volume);
                } else {
                    dates.add(latestDate);
                    closes.add(close);
                    volumes.add(volume);
                }

                entity.setTradeDates(dates.toArray(new LocalDate[0]));
                entity.setClose(closes.toArray(new Double[0]));
                entity.setVolume(volumes.toArray(new Double[0]));

                stockPriceRepository.save(entity);
            }

            // Update Analytics
            Analytics analytics = analyticsRepository.findById(symbol)
                    .orElseGet(() -> {
                        Analytics a = new Analytics();
                        a.setSymbol(symbol);
                        return a;
                    });

            analytics.setClosePrice(close);
            analytics.setVolume(volume);
            analytics.setPreviousClose(previousClose);
            analytics.setChangeAmount(changeAmount);
            analytics.setChangePercent(changePercent);
            analytics.setUpdatedAt(LocalDateTime.now());

            analyticsRepository.save(analytics);

            return true;

        } catch (Exception e) {
            System.out.println("Intraday update failed for " + symbol + ": " + e.getMessage());
            return false;
        }
    }


}
