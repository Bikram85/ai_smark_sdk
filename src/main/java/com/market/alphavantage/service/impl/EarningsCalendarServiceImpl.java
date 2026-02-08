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
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void loadEarningsCalendar(String horizon) {
        String url = baseUrl
                + "?function=EARNINGS_CALENDAR"
                + "&horizon=" + horizon
                + "&apikey=" + apiKey;

        logInfo("Fetching earnings calendar for horizon: " + horizon);

        String csv = restTemplate.getForObject(url, String.class);
        if (csv == null || csv.isBlank()) {
            logError("No CSV data returned for horizon: " + horizon);
            return;
        }

        int count = 0;
        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",", -1); // keep empty strings
                if (parts.length < 7) continue;

                String symbol = safeTrim(parts[0]);
                String name = safeTrim(parts[1]);
                LocalDate reportDate = parseDate(parts[2]);
                LocalDate fiscalDateEnding = parseDate(parts[3]);
                Double estimate = parseDouble(parts[4]);
                String currency = safeTrim(parts[5]);
                String timeOfDay = safeTrim(parts[6]);

                EarningsCalendar entity = new EarningsCalendar();
                entity.setSymbol(symbol);
                entity.setName(name);
                entity.setFiscalDateEnding(fiscalDateEnding != null ? fiscalDateEnding : LocalDate.now());
                entity.setEstimate(estimate != null ? estimate : 0.0);
                entity.setReportDate(reportDate != null ? reportDate : LocalDate.now()); // or some default
                entity.setTimeOfTheDay(timeOfDay != null ? timeOfDay : "");
                entity.setCurrency(currency != null ? currency : "USD");

                repository.save(entity);
                count++;
            }

            logInfo("Saved " + count + " earnings records for horizon: " + horizon);

        } catch (Exception e) {
            logError("Failed to parse Earnings Calendar CSV: " + e.getMessage());
        }
    }

    @Override
    public EarningsCalendarDTO getEarningsCalendar() {
        // Fetch all earnings for the given horizon (id starts with "horizon_")
        List<EarningsCalendar> earnings = repository.findAll();

        if (earnings.isEmpty()) {
            logInfo("No earnings data found for horizon: " );
            return null;
        }

        logInfo("Retrieved " + earnings.size() + " earnings records for horizon: " );

        return new EarningsCalendarDTO(earnings);
    }

    /* ===== Helper Methods ===== */

    private LocalDate parseDate(String val) {
        try {
            if (val == null || val.isBlank()) return null;
            return LocalDate.parse(val.trim(), DATE_FORMATTER);
        } catch (Exception ex) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            if (val == null || val.isBlank()) return null; // allow null
            double d = Double.parseDouble(val.trim());
            return Math.round(d * 100.0) / 100.0; // 2 decimals
        } catch (Exception ex) {
            return null; // return null if parsing fails
        }
    }

    private String safeTrim(String val) {
        return val == null ? "" : val.trim();
    }

    private void logInfo(String msg) {
        System.out.println("[" + java.time.LocalDateTime.now().format(LOG_FORMATTER) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + java.time.LocalDateTime.now().format(LOG_FORMATTER) + "] ERROR: " + msg);
    }
}
