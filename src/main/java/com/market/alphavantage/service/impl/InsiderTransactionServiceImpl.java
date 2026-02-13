package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.InsiderTransactionDTO;
import com.market.alphavantage.entity.InsiderTransaction;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.InsiderTransactionRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.InsiderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class InsiderTransactionServiceImpl implements InsiderTransactionService {

    private final InsiderTransactionRepository repository;
    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadInsiderTransactions() {
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");
        int total = stocks.size();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol -> processSymbol(symbol.getSymbol(), processed, success, failed, total));

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
            fetchDetails(symbol);
            success.incrementAndGet();
            logInfo("Processed " + current + "/" + total + " SUCCESS: " + symbol);
        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("Processed " + current + "/" + total + " FAILED: " + symbol + " Reason: " + ex.getMessage());
        }
    }

    private void fetchDetails(String symbol) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = baseUrl
                + "?function=INSIDER_TRANSACTIONS"
                + "&symbol=" + symbol.toUpperCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) {
            logError("No response for " + symbol);
            return;
        }

        List<Map<String, Object>> list =
                (List<Map<String, Object>>) response.get("data");

        if (list == null || list.isEmpty()) {
            logError("No insider transactions for " + symbol);
            return;
        }

        // -------- SORT BY DATE (2019 → 2020 → 2021 → latest) --------
        list = list.stream()
                .filter(r -> r.get("transaction_date") != null)
                .sorted((a, b) -> {
                    LocalDate d1 = parseDate((String) a.get("transaction_date"));
                    LocalDate d2 = parseDate((String) b.get("transaction_date"));
                    if (d1 == null) return -1;
                    if (d2 == null) return 1;
                    return d1.compareTo(d2);
                })
                .toList();

        InsiderTransaction e = new InsiderTransaction();
        e.setSymbol(symbol.toUpperCase());

        int size = list.size();

        LocalDate[] dates = new LocalDate[size];
        String[] names = new String[size];
        String[] relationships = new String[size];
        String[] types = new String[size];
        String[] ownerships = new String[size];
        Long[] transacted = new Long[size];
        Long[] owned = new Long[size];
        Double[] avgPrices = new Double[size];
        String[] titles = new String[size];

        for (int i = 0; i < size; i++) {
            Map<String, Object> r = list.get(i);

            dates[i] = parseDate((String) r.get("transaction_date"));
            names[i] = (String) r.getOrDefault("executive", "");
            titles[i] = (String) r.getOrDefault("executive_title", "");

            // Not provided by API
            relationships[i] = "";
            ownerships[i] = "";

            types[i] = (String) r.getOrDefault("acquisition_or_disposal", "");

            transacted[i] = parseLong(String.valueOf(r.get("shares")));
            owned[i] = 0L; // not available
            avgPrices[i] = parseDouble((String) r.get("share_price"));
        }

        e.setTransactionDates(dates);
        e.setInsiderNames(names);
        e.setRelationships(relationships);
        e.setTransactionTypes(types);
        e.setOwnershipTypes(ownerships);
        e.setSharesTransacted(transacted);
        e.setSharesOwned(owned);
        e.setAvgPrices(avgPrices);
        e.setReportedTitles(titles);

        repository.save(e);
        logInfo("Saved insider transactions for symbol: " + symbol);
    }


    @Override
    public InsiderTransactionDTO getInsiderTransactions(String symbol) {
        InsiderTransaction e = repository.findById(symbol.toUpperCase()).orElse(null);
        if (e == null) return null;

        return new InsiderTransactionDTO(
                e.getSymbol(),
                e.getTransactionDates() != null ? Arrays.asList(e.getTransactionDates()) : new ArrayList<>(),
                e.getInsiderNames() != null ? Arrays.asList(e.getInsiderNames()) : new ArrayList<>(),
                e.getRelationships() != null ? Arrays.asList(e.getRelationships()) : new ArrayList<>(),
                e.getTransactionTypes() != null ? Arrays.asList(e.getTransactionTypes()) : new ArrayList<>(),
                e.getOwnershipTypes() != null ? Arrays.asList(e.getOwnershipTypes()) : new ArrayList<>(),
                e.getSharesTransacted() != null ? Arrays.asList(e.getSharesTransacted()) : new ArrayList<>(),
                e.getSharesOwned() != null ? Arrays.asList(e.getSharesOwned()) : new ArrayList<>(),
                e.getAvgPrices() != null ? Arrays.asList(e.getAvgPrices()) : new ArrayList<>(),
                e.getReportedTitles() != null ? Arrays.asList(e.getReportedTitles()) : new ArrayList<>()
        );
    }

    /* ===== Helpers ===== */

    private void logInfo(String msg) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] INFO: " + msg);
    }

    private void logError(String msg) {
        System.err.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] ERROR: " + msg);
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.isBlank()) return null;
        try { return LocalDate.parse(val); }
        catch (Exception e) { return null; }
    }

    private Long parseLong(String val) {
        try { return val == null || val.isBlank() ? 0L : Long.parseLong(val); }
        catch (Exception e) { return 0L; }
    }

    private Double parseDouble(String val) {
        try { return val == null || val.isBlank() ? 0.0 : Double.parseDouble(val); }
        catch (Exception e) { return 0.0; }
    }
}
