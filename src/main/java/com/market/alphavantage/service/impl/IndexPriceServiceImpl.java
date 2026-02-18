package com.market.alphavantage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.alphavantage.dto.IndexPriceDTO;
import com.market.alphavantage.entity.IndexPrice;
import com.market.alphavantage.repository.IndexPriceRepository;
import com.market.alphavantage.util.IndexConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IndexPriceServiceImpl {

    private final IndexPriceRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    /**
     * Fetches all popular indices listed in IndexConstants and saves in DB.
     */
    public List<IndexPriceDTO> fetchAllPopularIndices() {
        for (IndexConstants idx : IndexConstants.POPULAR_INDICES) {
            try {
                System.out.println("Fetching: " + idx.getSymbol() + " (" + idx.getCountry() + ")");
                fetchAndSave(idx);
            } catch (Exception e) {
                System.err.println("Failed fetching index: " + idx.getSymbol());
                e.printStackTrace();
            }

        }
        List<IndexPrice> entities = repository.findAll();
        List<IndexPriceDTO> result = new ArrayList<>();
        for (IndexPrice e : entities) {
            result.add(toDTO(e));
        }
        return result;
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

    /**
     * Get saved index from DB.
     */
    public IndexPriceDTO getFromDB(String symbol) {
        IndexPrice entity = repository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Index not found: " + symbol));
        return toDTO(entity);
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
}
