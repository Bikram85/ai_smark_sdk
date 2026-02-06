package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.SplitDTO;
import com.market.alphavantage.entity.Split;
import com.market.alphavantage.repository.SplitRepository;
import com.market.alphavantage.service.SplitService;
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
public class SplitServiceImpl implements SplitService {

    private final SplitRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadSplits(String symbol) {
        String url = baseUrl
                + "?function=SPLITS"
                + "&symbol=" + symbol.toUpperCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        List<Map<String, String>> splits = (List<Map<String, String>>) response.get("splits");
        if (splits == null) return;

        Split entity = new Split();
        entity.setSymbol(symbol.toUpperCase());

        List<LocalDate> splitDates = new ArrayList<>();
        List<String> splitRatios = new ArrayList<>();

        for (Map<String, String> s : splits) {
            splitDates.add(parseDate(s.get("splitDate")));
            splitRatios.add(s.get("splitRatio"));
        }

        entity.setSplitDate(splitDates);
        entity.setSplitRatio(splitRatios);

        repository.save(entity);
    }

    @Override
    public SplitDTO getSplits(String symbol) {
        Split e = repository.findById(symbol.toUpperCase()).orElse(null);
        if (e == null) return null;

        return new SplitDTO(
                e.getSymbol(),
                e.getSplitDate(),
                e.getSplitRatio()
        );
    }

    private LocalDate parseDate(String val) {
        try {
            return val == null || val.isBlank() ? null : LocalDate.parse(val);
        } catch (Exception ex) {
            return null;
        }
    }
}
