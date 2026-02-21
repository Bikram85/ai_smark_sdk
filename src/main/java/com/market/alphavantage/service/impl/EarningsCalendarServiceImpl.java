package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.EarningsCalendarDTO;
import com.market.alphavantage.entity.CompanyOverview;
import com.market.alphavantage.entity.EarningsCalendar;
import com.market.alphavantage.repository.CompanyOverviewRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EarningsCalendarServiceImpl implements EarningsCalendarService {

    private final EarningsCalendarRepository repository;
    private final RestTemplate restTemplate;
    private final CompanyOverviewRepository companyRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void loadEarningsCalendar() {
        repository.deleteAll();
        String url = baseUrl
                + "?function=EARNINGS_CALENDAR"
                + "&horizon=" + "3month"
                + "&apikey=" + apiKey;

        logInfo("Fetching earnings calendar for horizon: 3 Months" );

        String csv = restTemplate.getForObject(url, String.class);
        if (csv == null || csv.isBlank()) {
            logError("No CSV data returned for horizon: 3 Months " );
            return ;
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
                entity.setReportDate(reportDate != null ? reportDate : LocalDate.now());
                entity.setTimeOfTheDay(timeOfDay != null ? timeOfDay : "");
                entity.setCurrency(currency != null ? currency : "USD");

                // ======= MARKET CAP CATEGORY =======
                try {
                    Optional<CompanyOverview> coOpt = companyRepo.findById(symbol);
                    Long marketCap = coOpt.map(CompanyOverview::getMarketCapitalization).orElse(null);
                    entity.setMarketCapCategory(determineMarketCapCategory(marketCap));
                } catch (Exception ex) {
                    entity.setMarketCapCategory("Unknown");
                }

                repository.save(entity);
                count++;
            }

            logInfo("Saved " + count + " earnings records for horizon: " + "3 Months");

        } catch (Exception e) {
            logError("Failed to parse Earnings Calendar CSV: " + e.getMessage());
        }


    }

    @Override
    public EarningsCalendarDTO loadEarningsCalendar(String horizon) {
        String url = baseUrl
                + "?function=EARNINGS_CALENDAR"
                + "&horizon=" + "3month"
                + "&apikey=" + apiKey;

        logInfo("Fetching earnings calendar for horizon: " + horizon);

        String csv = restTemplate.getForObject(url, String.class);
        if (csv == null || csv.isBlank()) {
            logError("No CSV data returned for horizon: " + horizon);
            return null;
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
                entity.setReportDate(reportDate != null ? reportDate : LocalDate.now());
                entity.setTimeOfTheDay(timeOfDay != null ? timeOfDay : "");
                entity.setCurrency(currency != null ? currency : "USD");

                // ======= MARKET CAP CATEGORY =======
                try {
                    Optional<CompanyOverview> coOpt = companyRepo.findById(symbol);
                    Long marketCap = coOpt.map(CompanyOverview::getMarketCapitalization).orElse(null);
                    entity.setMarketCapCategory(determineMarketCapCategory(marketCap));
                } catch (Exception ex) {
                    entity.setMarketCapCategory("Unknown");
                }

                repository.save(entity);
                count++;
            }

            logInfo("Saved " + count + " earnings records for horizon: " + horizon);

        } catch (Exception e) {
            logError("Failed to parse Earnings Calendar CSV: " + e.getMessage());
        }
        List<EarningsCalendar> earnings = repository.findAll();
        return new EarningsCalendarDTO(earnings);

    }

    @Override
    public EarningsCalendarDTO getEarningsCalendar() {
        List<EarningsCalendar> earnings = repository.findAll();

        if (earnings.isEmpty()) {
            logInfo("No earnings data found");
            return null;
        }

        logInfo("Retrieved " + earnings.size() + " earnings records");
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
            if (val == null || val.isBlank()) return null;
            double d = Double.parseDouble(val.trim());
            return Math.round(d * 100.0) / 100.0; // round to 2 decimals
        } catch (Exception ex) {
            return null;
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

    private String determineMarketCapCategory(Long cap) {
        if (cap == null) return "Unknown";
        if (cap < 50_000_000) return "Nano Cap";
        if (cap < 300_000_000) return "Micro Cap";
        if (cap < 2_000_000_000L) return "Small Cap";
        if (cap < 10_000_000_000L) return "Mid Cap";
        if (cap < 200_000_000_000L) return "Large Cap";
        return "Mega Cap";
    }
}
