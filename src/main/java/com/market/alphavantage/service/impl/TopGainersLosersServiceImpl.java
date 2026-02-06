package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.TopGainersLosersDTO;
import com.market.alphavantage.entity.TopGainersLosers;
import com.market.alphavantage.repository.TopGainersLosersRepository;

import com.market.alphavantage.service.TopGainersLosersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TopGainersLosersServiceImpl implements TopGainersLosersService {

    private final TopGainersLosersRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadTopGainersLosers() {
        String url = baseUrl + "?function=TOP_GAINERS_LOSERS&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) return;

        // Top gainers and losers typically have keys: "topGainers", "topLosers"
        saveList("top_gainers", (List<Map<String, String>>) response.get("topGainers"));
        saveList("top_losers", (List<Map<String, String>>) response.get("topLosers"));
    }

    private void saveList(String id, List<Map<String, String>> list) {
        if (list == null) return;

        TopGainersLosers entity = new TopGainersLosers();
        entity.setId(id);

        List<String> symbols = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        List<Double> changes = new ArrayList<>();
        List<Double> percentChanges = new ArrayList<>();
        List<Long> volumes = new ArrayList<>();

        for (Map<String, String> r : list) {
            symbols.add(r.get("symbol"));
            names.add(r.get("name"));
            prices.add(parseDouble(r.get("price")));
            changes.add(parseDouble(r.get("change")));
            percentChanges.add(parseDouble(r.get("percentChange")));
            volumes.add(parseLong(r.get("volume")));
        }

        entity.setSymbol(symbols);
        entity.setName(names);
        entity.setPrice(prices);
        entity.setChange(changes);
        entity.setPercentChange(percentChanges);
        entity.setVolume(volumes);

        repository.save(entity);
    }

    private Double parseDouble(String val) {
        try {
            return val == null || val.isBlank() ? 0.0 : Double.valueOf(val);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Long parseLong(String val) {
        try {
            return val == null || val.isBlank() ? 0L : Long.valueOf(val);
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public TopGainersLosersDTO getTopGainersLosers(String id) {
        return repository.findById(id)
                .map(e -> new TopGainersLosersDTO(
                        e.getId(),
                        e.getSymbol(),
                        e.getName(),
                        e.getPrice(),
                        e.getChange(),
                        e.getPercentChange(),
                        e.getVolume()
                ))
                .orElse(null);
    }
}
