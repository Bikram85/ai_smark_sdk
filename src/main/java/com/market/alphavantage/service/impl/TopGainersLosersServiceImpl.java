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

        saveList("gainer",
                (List<Map<String, String>>) response.get("top_gainers"),
                "top_gainers");

        saveList("loser",
                (List<Map<String, String>>) response.get("top_losers"),
                "top_losers");

        logInfo("Completed loadTopGainersLosers.");
    }

    /**
     * Save each symbol as a separate row
     */
    private void saveList(String id,
                          List<Map<String, String>> list,
                          String type) {

        if (list == null || list.isEmpty()) {
            logInfo("No data to save for " + type);
            return;
        }

        // Optionally: delete existing rows for this id before inserting fresh
        repository.deleteAll(repository.findById(id));

        for (Map<String, String> item : list) {
            TopGainersLosers entity = new TopGainersLosers();
            entity.setId(id); // "gainer" or "loser"
            entity.setSymbol(item.get("ticker"));
            entity.setName(item.getOrDefault("name", ""));
            entity.setPrice(parseDouble(item.get("price")));
            entity.setChange(parseDouble(item.get("change_amount")));
            entity.setPercentChange(parsePercent(item.get("change_percentage")));
            entity.setVolume(parseLong(item.get("volume")));

            repository.save(entity);
        }

        logInfo("Saved " + type + " with id: " + id + ", entries: " + list.size());
    }

    private Double parsePercent(String val) {
        if (val == null) return null;
        return Double.parseDouble(val.replace("%", ""));
    }

    @Override
    public TopGainersLosersDTO getTopGainersLosers(String id) {
        List<TopGainersLosers> entities = repository.findById(id);

        if (entities.isEmpty()) return null;

        return new TopGainersLosersDTO(
                id,
                entities.stream().map(TopGainersLosers::getSymbol).toList(),
                entities.stream().map(TopGainersLosers::getName).toList(),
                entities.stream().map(TopGainersLosers::getPrice).toList(),
                entities.stream().map(TopGainersLosers::getChange).toList(),
                entities.stream().map(TopGainersLosers::getPercentChange).toList(),
                entities.stream().map(TopGainersLosers::getVolume).toList()
        );
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
