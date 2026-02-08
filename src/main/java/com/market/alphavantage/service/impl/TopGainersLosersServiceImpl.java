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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadTopGainersLosers() {
        logInfo("Starting loadTopGainersLosers...");

        String url = baseUrl + "?function=TOP_GAINERS_LOSERS&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) {
            logError("No data returned from API.");
            return;
        }

        saveList("top_gainers", (List<Map<String, String>>) response.get("topGainers"), "topGainers");
        saveList("top_losers", (List<Map<String, String>>) response.get("topLosers"), "topLosers");

        logInfo("Completed loadTopGainersLosers.");
    }

    private void saveList(String id, List<Map<String, String>> list, String type) {
        if (list == null || list.isEmpty()) {
            logInfo("No data to save for " + type);
            return;
        }

        TopGainersLosers entity = new TopGainersLosers();
        entity.setId(id);
        entity.setType(type);

        // Convert lists to arrays directly
        entity.setSymbol(list.stream().map(r -> r.get("symbol")).toArray(String[]::new));
        entity.setName(list.stream().map(r -> r.get("name")).toArray(String[]::new));
        entity.setPrice(list.stream().map(r -> parseDouble(r.get("price"))).toArray(Double[]::new));
        entity.setChange(list.stream().map(r -> parseDouble(r.get("change"))).toArray(Double[]::new));
        entity.setPercentChange(list.stream().map(r -> parseDouble(r.get("percentChange"))).toArray(Double[]::new));
        entity.setVolume(list.stream().map(r -> parseLong(r.get("volume"))).toArray(Long[]::new));

        repository.save(entity);

        logInfo("Saved " + type + " with id: " + id + ", total entries: " + list.size());
    }

    @Override
    public TopGainersLosersDTO getTopGainersLosers(String id) {
        return repository.findById(id)
                .map(e -> new TopGainersLosersDTO(
                        e.getId(),
                        e.getSymbol() != null ? List.of(e.getSymbol()) : List.of(),
                        e.getName() != null ? List.of(e.getName()) : List.of(),
                        e.getPrice() != null ? List.of(e.getPrice()) : List.of(),
                        e.getChange() != null ? List.of(e.getChange()) : List.of(),
                        e.getPercentChange() != null ? List.of(e.getPercentChange()) : List.of(),
                        e.getVolume() != null ? List.of(e.getVolume()) : List.of()
                ))
                .orElse(null);
    }


    /* ===== Helpers ===== */

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

    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + msg);
    }
}
