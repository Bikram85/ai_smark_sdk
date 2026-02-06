package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.EquityTechnicalIndicatorDTO;
import com.market.alphavantage.entity.EquityTechnicalIndicator;
import com.market.alphavantage.repository.EquityTechnicalIndicatorRepository;
import com.market.alphavantage.service.EquityTechnicalIndicatorService;
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
public class EquityTechnicalIndicatorServiceImpl implements EquityTechnicalIndicatorService {

    private final EquityTechnicalIndicatorRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadSMA(String symbol, String interval, Integer timePeriod, String seriesType) {
        String url = baseUrl
                + "?function=SMA"
                + "&symbol=" + symbol.toUpperCase()
                + "&interval=" + interval.toLowerCase()
                + "&time_period=" + timePeriod
                + "&series_type=" + seriesType.toLowerCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        Map<String, Map<String, String>> series = (Map<String, Map<String, String>>) response.get("Technical Analysis: SMA");
        if (series == null) return;

        EquityTechnicalIndicator entity = new EquityTechnicalIndicator();
        String id = symbol.toUpperCase() + "_" + interval.toLowerCase() + "_" + timePeriod + "_" + seriesType.toLowerCase();
        entity.setId(id);
        entity.setSymbol(symbol.toUpperCase());
        entity.setInterval(interval.toLowerCase());
        entity.setTimePeriod(timePeriod);
        entity.setSeriesType(seriesType.toLowerCase());

        List<LocalDate> dates = new ArrayList<>();
        List<Double> smaList = new ArrayList<>();

        series.forEach((date, values) -> {
            dates.add(parseDate(date));
            smaList.add(parseDouble(values.get("SMA")));
        });

        entity.setDate(dates);
        entity.setSma(smaList);

        repository.save(entity);
    }

    @Override
    public EquityTechnicalIndicatorDTO getSMA(String symbol, String interval, Integer timePeriod, String seriesType) {
        String id = symbol.toUpperCase() + "_" + interval.toLowerCase() + "_" + timePeriod + "_" + seriesType.toLowerCase();
        EquityTechnicalIndicator e = repository.findById(id).orElse(null);
        if (e == null) return null;

        return new EquityTechnicalIndicatorDTO(
                e.getId(),
                e.getSymbol(),
                e.getInterval(),
                e.getTimePeriod(),
                e.getSeriesType(),
                e.getDate(),
                e.getSma()
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
}
