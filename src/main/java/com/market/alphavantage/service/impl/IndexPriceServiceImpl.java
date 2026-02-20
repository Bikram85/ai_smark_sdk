package com.market.alphavantage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.alphavantage.dto.IndexPriceDTO;
import com.market.alphavantage.entity.IndexPrice;
import com.market.alphavantage.repository.IndexPriceRepository;
import com.market.alphavantage.util.IndexConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.market.alphavantage.yahoo.impl.StockPriceImpl;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IndexPriceServiceImpl {

    private final IndexPriceRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    StockPriceImpl stockPrice;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    /**
     * Fetches all popular indices listed in IndexConstants and saves in DB.
     */
    public void fetchAllPopularIndices() {
        for (IndexConstants idx : IndexConstants.POPULAR_INDICES) {
            try {
                System.out.println("Fetching: " + idx.getSymbol() + " (" + idx.getCountry() + ")");
                fetchAndSave(idx);
            } catch (Exception e) {
                System.err.println("Failed fetching index: " + idx.getSymbol());
                e.printStackTrace();
            }

        }

    }

    public void fetchAllPopularIndicesIntraday() {
        for (IndexConstants idx : IndexConstants.POPULAR_INDICES) {
            try {
                System.out.println("Fetching: " + idx.getSymbol() + " (" + idx.getCountry() + ")");
                fetchAndUpdateIntraday(idx);
            } catch (Exception e) {
                System.err.println("Failed fetching index: " + idx.getSymbol());
                e.printStackTrace();
            }

        }

    }

    /**
     * Fetches index data from Alpha Vantage API and saves to DB.
     */
    public void fetchAndSave(IndexConstants idx) {
        try {
            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED"
                    + "&symbol=" + idx.getSymbol()
                    + "&outputsize=full"
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = mapper.readTree(response);
            JsonNode series = root.get("Time Series (Daily)");

            if (series == null)
                throw new RuntimeException("Invalid API response for " + idx.getSymbol());

            List<String> dates = new ArrayList<>();
            List<Double> open = new ArrayList<>();
            List<Double> high = new ArrayList<>();
            List<Double> low = new ArrayList<>();
            List<Double> close = new ArrayList<>();
            List<Long> volume = new ArrayList<>();

            Iterator<String> fields = series.fieldNames();
            while (fields.hasNext()) {
                String date = fields.next();
                JsonNode day = series.get(date);

                dates.add(date);
                open.add(day.get("1. open").asDouble());
                high.add(day.get("2. high").asDouble());
                low.add(day.get("3. low").asDouble());
                close.add(day.get("4. close").asDouble());
                volume.add(day.get("6. volume").asLong()); // Adjusted API key for volume
            }

            // Ensure oldest first
            Collections.reverse(dates);
            Collections.reverse(open);
            Collections.reverse(high);
            Collections.reverse(low);
            Collections.reverse(close);
            Collections.reverse(volume);

            IndexPrice entity = IndexPrice.builder()
                    .symbol(idx.getSymbol())
                    .country(idx.getCountry())
                    .name(idx.getName())
                    .dates(dates.toArray(new String[0]))
                    .open(open.toArray(new Double[0]))
                    .high(high.toArray(new Double[0]))
                    .low(low.toArray(new Double[0]))
                    .close(close.toArray(new Double[0]))
                    .volume(volume.toArray(new Long[0]))
                    .build();

            repository.save(entity);



        } catch (Exception e) {
            throw new RuntimeException("Failed fetching index data for " + idx.getSymbol(), e);
        }
    }

    public void fetchAndUpdateIntraday(IndexConstants idx) {


        try {

            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY"
                    + "&symbol=" + idx.getSymbol()
                    + "&interval=5min"
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);

            // 🔥 Check API limit error
            if (root.has("Note") || root.has("Error Message")) {
                System.out.println("API limit hit or error for " + idx.getSymbol());
                return;
            }

            JsonNode meta = root.get("Meta Data");
            JsonNode series = root.get("Time Series (5min)");

            if (meta == null || series == null) return;

            // ✅ Get exact latest timestamp safely
            String latestTimestamp = meta.get("3. Last Refreshed").asText();

            JsonNode candle = series.get(latestTimestamp);
            if (candle == null) return;

            double open = candle.get("1. open").asDouble();
            double high = candle.get("2. high").asDouble();
            double low = candle.get("3. low").asDouble();
            double close = candle.get("4. close").asDouble();
            long volume = candle.get("5. volume").asLong();

            String todayDate = latestTimestamp.substring(0, 10);

            Optional<IndexPrice> optional = repository.findById(idx.getSymbol());
            if (optional.isEmpty()) return;

            IndexPrice entity = optional.get();

            List<String> dates = new ArrayList<>(Arrays.asList(entity.getDates()));
            List<Double> opens = new ArrayList<>(Arrays.asList(entity.getOpen()));
            List<Double> highs = new ArrayList<>(Arrays.asList(entity.getHigh()));
            List<Double> lows = new ArrayList<>(Arrays.asList(entity.getLow()));
            List<Double> closes = new ArrayList<>(Arrays.asList(entity.getClose()));
            List<Long> volumes = new ArrayList<>(Arrays.asList(entity.getVolume()));

            int lastIndex = dates.size() - 1;

            // ✅ If today exists → update
            if (!dates.isEmpty() && dates.get(lastIndex).equals(todayDate)) {

                highs.set(lastIndex, Math.max(highs.get(lastIndex), high));
                lows.set(lastIndex, Math.min(lows.get(lastIndex), low));
                closes.set(lastIndex, close);

                // ⚠️ Do NOT blindly add full volume (intraday is cumulative)
                volumes.set(lastIndex, volume);

            } else {

                // Append new trading day
                dates.add(todayDate);
                opens.add(open);
                highs.add(high);
                lows.add(low);
                closes.add(close);
                volumes.add(volume);
            }

            entity.setDates(dates.toArray(new String[0]));
            entity.setOpen(opens.toArray(new Double[0]));
            entity.setHigh(highs.toArray(new Double[0]));
            entity.setLow(lows.toArray(new Double[0]));
            entity.setClose(closes.toArray(new Double[0]));
            entity.setVolume(volumes.toArray(new Long[0]));

            repository.save(entity);

            System.out.println("Updated intraday for " + idx.getSymbol());

        } catch (Exception e) {
            System.out.println("Intraday update failed: " + e.getMessage());
        }
    }

    /**
     * Get saved index from DB.
     */
    public List<IndexPriceDTO> getFromDB() {

        List<IndexPrice> entities = repository.findAll();

        if (entities.isEmpty()) {
            throw new RuntimeException("No index data found");
        }

        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Get all saved indices from DB
     */
    public List<IndexPriceDTO> getAllIndices() {
        List<IndexPrice> entities = repository.findAll();
        List<IndexPriceDTO> result = new ArrayList<>();
        for (IndexPrice e : entities) {
            result.add(toDTO(e));
        }
        return result;
    }

    private IndexPriceDTO toDTO(IndexPrice e) {
        return IndexPriceDTO.builder()
                .symbol(e.getSymbol())
                .name(e.getName())
                .country(e.getCountry())
                .dates(Arrays.asList(e.getDates()))
                .open(Arrays.asList(e.getOpen()))
                .high(Arrays.asList(e.getHigh()))
                .low(Arrays.asList(e.getLow()))
                .close(Arrays.asList(e.getClose()))
                .volume(Arrays.asList(e.getVolume()))
                .build();
    }

    public void captureIndex(){

        //  100 biggest companies by market capitalisation on the London Stock Exchange (LSE).
        stockPrice.captureTickerPriceFromYahoo("^FTSE");
        //  Index also gives a general idea of the direction of the Euronext Paris,
        //  the largest stock exchange in France formerly known as the Paris Bourse..
        stockPrice.captureTickerPriceFromYahoo("^FCHI");
        //Xetra is a fully electronic trading platform. Headquartered in Frankfurt, Germany,
        // the exchange is operated by Deutsche Börse Group, which also owns the Frankfurt Stock Exchange (FRA) or Frankfurter Wertpapierbörse
        stockPrice.captureTickerPriceFromYahoo("^GDAXI");
        //Swiss Market Index
        stockPrice.captureTickerPriceFromYahoo("^SSMI");
        //stock index consisting of the largest and most liquid companies on the Italian national stock exchange
        stockPrice.captureTickerPriceFromYahoo("FTSEMIB.MI");
        //Spanish stock exchange
        stockPrice.captureTickerPriceFromYahoo("^IBEX");

        //Dow Jones Industrial Average (^DJI)
        stockPrice.captureTickerPriceFromYahoo("^DJI");
        //Russell 2000 (^RUT)
        stockPrice.captureTickerPriceFromYahoo("^RUT");
        // FTSE 100 (^FTSE)
        stockPrice.captureTickerPriceFromYahoo("^FTSE");
        //Chines 500
        stockPrice.captureTickerPriceFromYahoo("GXC");
        //india
        stockPrice.captureTickerPriceFromYahoo("^CRSLDX");
        //SPY
        stockPrice.captureTickerPriceFromYahoo("^GSPC");
        //Nikke
        stockPrice.captureTickerPriceFromYahoo("^N225");
        //Nasdaq Compisite index
        stockPrice.captureTickerPriceFromYahoo("^IXIC");
        //Nasdaq Compisite index
        stockPrice.captureTickerPriceFromYahoo("^VIX");





    }

}
