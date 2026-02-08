package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.SharesOutstandingDTO;
import com.market.alphavantage.entity.SharesOutstanding;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.SharesOutstandingRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.SharesOutstandingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class SharesOutstandingServiceImpl implements SharesOutstandingService {

    private final SharesOutstandingRepository repository;
    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadSharesOutstanding() {
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
            logInfo("loadSharesOutstanding Processed " + current + "/" + total + " SUCCESS: " + symbol);
        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("loadSharesOutstanding Processed " + current + "/" + total + " FAILED: " + symbol
                    + " Reason: " + ex.getMessage());
        }
    }

    private void fetchDetails(String symbol) {

        String url = baseUrl
                + "?function=SHARES_OUTSTANDING"
                + "&symbol=" + symbol.toUpperCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        // Correct key
        List<Map<String, Object>> reports =
                (List<Map<String, Object>>) response.get("data");

        if (reports == null || reports.isEmpty()) return;

        // Sort oldest → newest (2019 → 2020 → 2021)
        reports.sort((a, b) -> {
            LocalDate d1 = parseDate((String) a.get("date"));
            LocalDate d2 = parseDate((String) b.get("date"));
            if (d1 == null) return -1;
            if (d2 == null) return 1;
            return d1.compareTo(d2);
        });

        SharesOutstanding entity =
                repository.findById(symbol.toUpperCase())
                        .orElse(new SharesOutstanding());

        entity.setSymbol(symbol.toUpperCase());

        List<LocalDate> dateList = new ArrayList<>();
        List<Long> sharesList = new ArrayList<>();

        for (Map<String, Object> r : reports) {

            LocalDate date = parseDate((String) r.get("date"));
            if (date == null) continue;

            dateList.add(date);

            sharesList.add(parseLong(
                    r.get("shares_outstanding_diluted") != null
                            ? r.get("shares_outstanding_diluted").toString()
                            : "0"
            ));
        }

        entity.setFiscalDates(dateList.toArray(new LocalDate[0]));
        entity.setReportedShares(sharesList.toArray(new Long[0]));

        repository.save(entity);

        logInfo("Saved shares outstanding for " + symbol);
    }


    @Override
    public SharesOutstandingDTO getSharesOutstanding(String symbol) {
        return repository.findById(symbol.toUpperCase())
                .map(e -> new SharesOutstandingDTO(
                        e.getSymbol(),
                        e.getFiscalDates() != null ? Arrays.asList(e.getFiscalDates()) : new ArrayList<>(),
                        e.getReportedShares() != null ? Arrays.asList(e.getReportedShares()) : new ArrayList<>()
                ))
                .orElse(null);
    }

    /* ===== Helpers ===== */

    private void logInfo(String message) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + message);
    }

    private void logError(String message) {
        System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + message);
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.isBlank()) return null;
        try { return LocalDate.parse(val); }
        catch (Exception e) { return null; }
    }

    private Long parseLong(String val) {
        if (val == null || val.isBlank() || val.equals("None")) return 0L;
        try { return Long.parseLong(val); }
        catch (Exception e) { return 0L; }
    }
}
