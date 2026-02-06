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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Override
    public void loadCommodity(String function, String interval) {
        String url = baseUrl
                + "?function=" + function.toUpperCase()
                + "&interval=" + interval.toLowerCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        // The key is usually "Time Series (Interval)" e.g., "Time Series (Monthly)"
        String key = "Time Series (" + interval.substring(0, 1).toUpperCase() + interval.substring(1) + ")";
        Map<String, Map<String, String>> series = (Map<String, Map<String, String>>) response.get(key);
        if (series == null) return;

        Commodity entity = new Commodity();
        String id = function.toUpperCase() + "_" + interval.toLowerCase();
        entity.setId(id);
        entity.setFunction(function.toUpperCase());
        entity.setInterval(interval.toLowerCase());

        List<LocalDate> tradeDates = new ArrayList<>();
        List<Double> opens = new ArrayList<>();
        List<Double> highs = new ArrayList<>();
        List<Double> lows = new ArrayList<>();
        List<Double> closes = new ArrayList<>();
        List<Long> volumes = new ArrayList<>();

        series.forEach((date, values) -> {
            tradeDates.add(parseDate(date));
            opens.add(parseDouble(values.get("1. open")));
            highs.add(parseDouble(values.get("2. high")));
            lows.add(parseDouble(values.get("3. low")));
            closes.add(parseDouble(values.get("4. close")));
            volumes.add(parseLong(values.get("5. volume")));
        });

        entity.setTradeDate(tradeDates);
        entity.setOpen(opens);
        entity.setHigh(highs);
        entity.setLow(lows);
        entity.setClose(closes);
        entity.setVolume(volumes);

        repository.save(entity);
    }

    @Override
    public CommodityDTO getCommodity(String function, String interval) {
        String id = function.toUpperCase() + "_" + interval.toLowerCase();
        Commodity e = repository.findById(id).orElse(null);
        if (e == null) return null;

        return new CommodityDTO(
                e.getId(),
                e.getFunction(),
                e.getInterval(),
                e.getTradeDate(),
                e.getOpen(),
                e.getHigh(),
                e.getLow(),
                e.getClose(),
                e.getVolume()
        );
    }

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

    private Long parseLong(String val) {
        try {
            return val == null || val.isBlank() ? 0L : Long.valueOf(val);
        } catch (Exception ex) {
            return 0L;
        }
    }
}
