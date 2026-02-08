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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadDividends() {
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");
        int total = stocks.size();

        AtomicInteger processed = new AtomicInteger();
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        stocks.forEach(symbol ->
                processSymbol(symbol.getSymbol(), processed, success, failed, total));

        logInfo("===== SUMMARY =====");
        logInfo("Total symbols : " + total);
        logInfo("Success       : " + success.get());
        logInfo("Failed        : " + failed.get());
    }

    private void processSymbol(String symbol,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {
        int current = processed.incrementAndGet();
        try {
            fetchDetails(symbol.toUpperCase());
            success.incrementAndGet();
            logInfo("loadDividends Processed " + current + "/" + total + " SUCCESS: " + symbol);
        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("loadDividends Processed " + current + "/" + total + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }

    private void fetchDetails(String symbol) {
        String url = String.format("%s?function=DIVIDENDS&symbol=%s&apikey=%s", baseUrl, symbol, apiKey);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        // Correct key is "data"
        List<Map<String, Object>> dividends = (List<Map<String, Object>>) response.get("data");
        if (dividends == null || dividends.isEmpty()) return;

        // Sort dividends by ex_dividend_date ascending (oldest first)
        dividends = dividends.stream()
                .filter(d -> d.get("ex_dividend_date") != null && d.get("amount") != null)
                .sorted((d1, d2) -> {
                    LocalDate date1 = parseDate((String) d1.get("ex_dividend_date"));
                    LocalDate date2 = parseDate((String) d2.get("ex_dividend_date"));
                    return date1.compareTo(date2);
                })
                .collect(Collectors.toList());

        // Parse ex_dividend_date
        LocalDate[] exDates = dividends.stream()
                .map(d -> parseDate((String) d.get("ex_dividend_date")))
                .toArray(LocalDate[]::new);

        // Parse dividend amount
        Double[] amounts = dividends.stream()
                .map(d -> parseDouble((String) d.get("amount")))
                .toArray(Double[]::new);

        if (exDates.length == 0 || amounts.length == 0) return;

        Dividend entity = new Dividend();
        entity.setSymbol(symbol);
        entity.setExDividendDates(exDates);
        entity.setDividendAmounts(amounts);

        repository.save(entity);
    }

    // Helper methods
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Double parseDouble(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public DividendDTO getDividends(String symbol) {
        Dividend e = repository.findById(symbol.toUpperCase()).orElse(null);
        if (e == null) return null;

        // Convert arrays to List for DTO
        List<LocalDate> exDates = e.getExDividendDates() != null ?
                List.of(e.getExDividendDates()) : List.of();

        List<Double> amounts = e.getDividendAmounts() != null ?
                List.of(e.getDividendAmounts()) : List.of();

        return new DividendDTO(e.getSymbol(), exDates, amounts);
    }

    /* ===== Helpers ===== */

    private void logInfo(String message) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + message);
    }

    private void logError(String message) {
        System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + message);
    }


}
