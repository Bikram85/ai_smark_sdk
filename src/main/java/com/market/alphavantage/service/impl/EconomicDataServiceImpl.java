package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.EconomicDataDTO;
import com.market.alphavantage.entity.EconomicData;
import com.market.alphavantage.repository.EconomicDataRepository;
import com.market.alphavantage.service.EconomicDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EconomicDataServiceImpl implements EconomicDataService {

    private final EconomicDataRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Load all configured economic indicators from Alpha Vantage.
     */
    @Override
    public void loadEconomicData() {
        loadData("REAL_GDP", "quarterly");
        loadData("REAL_GDP_PER_CAPITA", "quarterly");
        loadData("TREASURY_YIELD", "monthly");
        loadData("FEDERAL_FUNDS_RATE", "monthly");
        loadData("CPI", "monthly");
        loadData("INFLATION", "monthly");
        loadData("RETAIL_SALES", "monthly");
        loadData("DURABLES", "monthly");
        loadData("UNEMPLOYMENT", "monthly");
        loadData("NONFARM_PAYROLL", "monthly");
    }

    /**
     * Load data for a single economic function/interval and save into DB.
     */
    public void loadData(String function, String interval) {
        try {
            String url = baseUrl + "?function=" + function;
            if (interval != null && !interval.isEmpty()) {
                url += "&interval=" + interval;
            }
            url += "&apikey=" + apiKey;

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.isEmpty()) {
                System.err.println("No data from API for " + function);
                return;
            }

            Map<String, String> timeSeries = extractTimeSeries(response);
            if (timeSeries.isEmpty()) return;

            List<LocalDate> datesList = new ArrayList<>();
            List<Double> valuesList = new ArrayList<>();

            // Sort by date ascending (oldest → latest)
            timeSeries.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        LocalDate date;
                        String key = entry.getKey();
                        Double value = roundDouble(entry.getValue());
                        if (value == null) return; // skip invalid

                        if ("annual".equalsIgnoreCase(interval)) {
                            // Annual -> set to Jan 1 of year
                            date = LocalDate.parse(key, yearFormatter).withDayOfYear(1);
                        } else {
                            // Quarterly/monthly -> parse as yyyy-MM-dd
                            date = LocalDate.parse(key, monthFormatter);
                        }

                        datesList.add(date);
                        valuesList.add(value);
                    });

            if (datesList.isEmpty()) {
                System.out.println("No valid data points for " + function);
                return;
            }

            EconomicData entity = new EconomicData();
            entity.setSymbol(function);
            entity.setName(function.replace("_", " "));
            entity.setInterval(interval);
            entity.setDates(datesList.toArray(new LocalDate[0]));
            entity.setValues(valuesList.toArray(new Double[0]));

            repository.save(entity);

            System.out.println("Saved economic data for " + function + " (" + interval + ")");
        } catch (Exception e) {
            System.err.println("Error loading economic data for " + function + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract time series from Alpha Vantage response.
     * Skips invalid/missing values (".") and keeps old → new order.
     */
    private Map<String, String> extractTimeSeries(Map<String, Object> response) {
        Object dataObj = response.get("data");
        if (!(dataObj instanceof List)) return Collections.emptyMap();

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataObj;
        Map<String, String> timeSeries = new LinkedHashMap<>();

        for (Map<String, Object> m : dataList) {
            String date = m.get("date") != null ? m.get("date").toString() : null;
            String value = m.get("value") != null ? m.get("value").toString() : null;

            if (date != null && value != null && !value.trim().isEmpty() && !value.trim().equals(".")) {
                timeSeries.put(date, value);
            }
        }

        return timeSeries;
    }

    /**
     * Round to 2 decimal places. Returns null if invalid number.
     */
    private Double roundDouble(String val) {
        try {
            return Math.round(Double.parseDouble(val) * 100.0) / 100.0;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return all economic data in DTO format.
     */
    @Override
    public List<EconomicDataDTO> getAllEconomicData() {
        return repository.findAll().stream()
                .map(e -> new EconomicDataDTO(
                        e.getSymbol(),
                        e.getName(),
                        e.getInterval(),
                        e.getDates(),
                        e.getValues()
                ))
                .collect(Collectors.toList());
    }
}
