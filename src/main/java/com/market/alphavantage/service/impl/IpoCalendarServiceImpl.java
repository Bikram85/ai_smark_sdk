package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.IpoCalendarDTO;
import com.market.alphavantage.entity.IpoCalendar;
import com.market.alphavantage.repository.IpoCalendarRepository;
import com.market.alphavantage.service.IpoCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IpoCalendarServiceImpl implements IpoCalendarService {

    private final IpoCalendarRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadIpoCalendar() {
        String url = baseUrl + "?function=IPO_CALENDAR&apikey=" + "demo";
        String csv = restTemplate.getForObject(url, String.class);

        if (csv == null || csv.isEmpty()) {
            logInfo("IPO calendar CSV is empty");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String header = reader.readLine(); // skip header
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);

                IpoCalendar entity = new IpoCalendar();
                entity.setSymbol(p[0]); // primary key
                entity.setName(p[1]);
                entity.setIpoDate(parseDate(p[2]));
                entity.setPrice(p[3]);
                entity.setShares(p[4]);
                entity.setExchange(p[5]);
                entity.setCurrency(p[6]);

                repository.save(entity);
                count++;
            }

            logInfo("Saved " + count + " IPO records successfully.");

        } catch (Exception e) {
            logError("Failed to load IPO calendar: " + e.getMessage());
        }
    }

    @Override
    public IpoCalendarDTO getIpoCalendar() {
        List<IpoCalendar> all = repository.findAll();
        logInfo("Retrieved " + all.size() + " IPO records.");
        return new IpoCalendarDTO(all);
    }

    private LocalDate parseDate(String v) {
        try {
            return (v == null || v.isBlank()) ? null : LocalDate.parse(v);
        } catch (Exception e) {
            return null;
        }
    }

    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(logFormatter) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(logFormatter) + "] ERROR: " + msg);
    }
}
