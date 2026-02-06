package com.market.alphavantage.service.impl;



import com.market.alphavantage.dto.EarningsCalendarDTO;
import com.market.alphavantage.entity.EarningsCalendar;
import com.market.alphavantage.repository.EarningsCalendarRepository;

import com.market.alphavantage.service.EarningsCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EarningsCalendarServiceImpl implements EarningsCalendarService {

    private final EarningsCalendarRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadEarningsCalendar(String horizon) {

        // Build identifier for storage
        String id = "horizon_" + horizon;

        String url = baseUrl
                + "?function=EARNINGS_CALENDAR"
                + "&horizon=" + horizon
                + "&apikey=" + apiKey;

        // This API returns CSV (no JSON)
        String csv = restTemplate.getForObject(url, String.class);

        if (csv == null || csv.isEmpty()) return;

        List<String> symbols = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<LocalDate> reportDates = new ArrayList<>();
        List<LocalDate> fiscalDates = new ArrayList<>();
        List<Double> estimates = new ArrayList<>();
        List<String> currencies = new ArrayList<>();
        List<String> timeOfDay = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            // Typically first line is header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");

                // Symbol,Name,reportDate,fiscalDateEnding,estimate,currency,timeOfTheDay
                symbols.add(parts[0]);
                names.add(parts[1]);
                reportDates.add(parseDate(parts[2]));
                fiscalDates.add(parseDate(parts[3]));

                estimates.add(parseDouble(parts[4]));
                currencies.add(parts[5]);
                // Some CSVs include timeOfDay or blank
                timeOfDay.add(parts.length > 6 ? parts[6] : null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Earnings Calendar CSV", e);
        }

        EarningsCalendar entity = new EarningsCalendar();
        entity.setId(id);
        entity.setSymbol(symbols);
        entity.setName(names);
        entity.setReportDate(reportDates);
        entity.setFiscalDateEnding(fiscalDates);
        entity.setEstimate(estimates);
        entity.setCurrency(currencies);
        entity.setTimeOfTheDay(timeOfDay);

        repository.save(entity);
    }

    @Override
    public EarningsCalendarDTO getEarningsCalendar(String id) {
        return repository.findById(id)
                .map(e -> new EarningsCalendarDTO(
                        e.getId(),
                        e.getSymbol(),
                        e.getName(),
                        e.getReportDate(),
                        e.getFiscalDateEnding(),
                        e.getEstimate(),
                        e.getCurrency(),
                        e.getTimeOfTheDay()
                ))
                .orElse(null);
    }

    private LocalDate parseDate(String val) {
        try {
            return (val == null || val.isBlank()) ? null : LocalDate.parse(val);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return (val == null || val.isBlank()) ? null : Double.valueOf(val);
        } catch (Exception e) {
            return null;
        }
    }
}

