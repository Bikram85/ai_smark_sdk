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
import java.util.ArrayList;
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

    @Override
    public void loadIpoCalendar() {

        String id = "ipo_calendar_all";

        String url = baseUrl
                + "?function=IPO_CALENDAR"
                + "&apikey=" + apiKey;

        String csv = restTemplate.getForObject(url, String.class);
        if (csv == null || csv.isEmpty()) return;

        List<String> symbols = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<LocalDate> ipoDates = new ArrayList<>();
        List<String> prices = new ArrayList<>();
        List<String> shares = new ArrayList<>();
        List<String> exchanges = new ArrayList<>();
        List<String> currencies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String header = reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");

                symbols.add(p[0]);
                names.add(p[1]);
                ipoDates.add(parseDate(p[2]));
                prices.add(p[3]);
                shares.add(p[4]);
                exchanges.add(p[5]);
                currencies.add(p[6]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse IPO calendar CSV", e);
        }

        IpoCalendar entity = new IpoCalendar();
        entity.setId(id);
        entity.setSymbol(symbols);
        entity.setName(names);
        entity.setIpoDate(ipoDates);
        entity.setPrice(prices);
        entity.setShares(shares);
        entity.setExchange(exchanges);
        entity.setCurrency(currencies);

        repository.save(entity);
    }

    @Override
    public IpoCalendarDTO getIpoCalendar(String id) {
        return repository.findById(id)
                .map(e -> new IpoCalendarDTO(
                        e.getId(),
                        e.getSymbol(),
                        e.getName(),
                        e.getIpoDate(),
                        e.getPrice(),
                        e.getShares(),
                        e.getExchange(),
                        e.getCurrency()
                ))
                .orElse(null);
    }

    private LocalDate parseDate(String v) {
        try {
            return v == null || v.isBlank() ? null : LocalDate.parse(v);
        } catch (Exception e) {
            return null;
        }
    }
}
