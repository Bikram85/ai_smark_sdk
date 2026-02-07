package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.DividendDTO;
import com.market.alphavantage.entity.Dividend;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.DividendRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.DividendService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class DividendServiceImpl implements DividendService {

    private final DividendRepository repository;
    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;


    @Override
    public void loadDividends() {

        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");

        int total = stocks.size();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol -> {
            processSymbol(symbol.getSymbol(), "Stock",
                    processed, success, failed, total);
        });


        System.out.println("\n===== SUMMARY =====");
        System.out.println("Total symbols : " + total);
        System.out.println("Success       : " + success.get());
        System.out.println("Failed        : " + failed.get());
    }

    private void processSymbol(String symbol,
                               String type,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {

        int current = processed.incrementAndGet();

        try {
            fetchDetails(symbol);
            success.incrementAndGet();

            System.out.println("loadDividends Processed "
                    + current + "/" + total
                    + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();

            System.err.println("loadDividends Processed "
                    + current + "/" + total
                    + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }




    private void fetchDetails(String symbol) {
        String url = baseUrl
                + "?function=DIVIDENDS"
                + "&symbol=" + symbol.toUpperCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        List<Map<String, String>> dividends = (List<Map<String, String>>) response.get("dividends");
        if (dividends == null) return;

        Dividend entity = new Dividend();
        entity.setSymbol(symbol.toUpperCase());

        List<LocalDate> exDates = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        for (Map<String, String> d : dividends) {
            exDates.add(parseDate(d.get("exDate")));
            amounts.add(parseDouble(d.get("dividendAmount")));
        }

        entity.setExDividendDate(exDates);
        entity.setDividendAmount(amounts);

        repository.save(entity);
    }

    @Override
    public DividendDTO getDividends(String symbol) {
        Dividend e = repository.findById(symbol.toUpperCase()).orElse(null);
        if (e == null) return null;

        return new DividendDTO(
                e.getSymbol(),
                e.getExDividendDate(),
                e.getDividendAmount()
        );
    }

    private LocalDate parseDate(String val) {
        try {
            return val == null || val.isBlank() ? null : LocalDate.parse(val);
        } catch (Exception ex) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return val == null || val.isBlank() ? 0.0 : Double.valueOf(val);
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
