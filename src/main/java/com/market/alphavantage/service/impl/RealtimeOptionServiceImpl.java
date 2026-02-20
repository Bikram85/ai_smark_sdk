package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.RealtimeOptionDTO;
import com.market.alphavantage.entity.RealtimeOption;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.RealtimeOptionRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.RealtimeOptionService;
import com.market.alphavantage.service.impl.processor.OptionProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class RealtimeOptionServiceImpl implements RealtimeOptionService {

    private final RealtimeOptionRepository repository;
    private final RestTemplate restTemplate;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Autowired
    OptionProcessor optionProcessor;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadRealtimeOptions() {
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");
        int total = stocks.size();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol -> processSymbol(symbol.getSymbol(), processed, success, failed, total));

        logInfo("\n===== SUMMARY =====");
        logInfo("Total loadRealtimeOptions symbols : " + total);
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
            optionProcessor.fetchDetails(symbol);
            success.incrementAndGet();
            logInfo("Processed loadRealtimeOptions " + current + "/" + total + " SUCCESS: " + symbol);
        } catch (Exception ex) {
            failed.incrementAndGet();
            logError("Processed loadRealtimeOptions " + current + "/" + total + " FAILED: " + symbol + " Reason: " + ex.getMessage());
        }
    }




    @Override
    public RealtimeOptionDTO getRealtimeOptions(String symbol) {
        RealtimeOption e = repository.findById(symbol.toUpperCase()).orElse(null);
        if (e == null) return null;

        return new RealtimeOptionDTO(
                e.getSymbol(),

                // Calls
                e.getCallExpirationDate() != null ? Arrays.asList(e.getCallExpirationDate()) : new ArrayList<>(),
                e.getCallStrikePrice() != null ? Arrays.asList(e.getCallStrikePrice()) : new ArrayList<>(),
                e.getCallLastPrice() != null ? Arrays.asList(e.getCallLastPrice()) : new ArrayList<>(),
                e.getCallBid() != null ? Arrays.asList(e.getCallBid()) : new ArrayList<>(),
                e.getCallAsk() != null ? Arrays.asList(e.getCallAsk()) : new ArrayList<>(),
                e.getCallVolume() != null ? Arrays.asList(e.getCallVolume()) : new ArrayList<>(),
                e.getCallOpenInterest() != null ? Arrays.asList(e.getCallOpenInterest()) : new ArrayList<>(),
                e.getCallImpliedVolatility() != null ? Arrays.asList(e.getCallImpliedVolatility()) : new ArrayList<>(),
                e.getCallDelta() != null ? Arrays.asList(e.getCallDelta()) : new ArrayList<>(),
                e.getCallGamma() != null ? Arrays.asList(e.getCallGamma()) : new ArrayList<>(),
                e.getCallTheta() != null ? Arrays.asList(e.getCallTheta()) : new ArrayList<>(),
                e.getCallVega() != null ? Arrays.asList(e.getCallVega()) : new ArrayList<>(),

                // Puts
                e.getPutExpirationDate() != null ? Arrays.asList(e.getPutExpirationDate()) : new ArrayList<>(),
                e.getPutStrikePrice() != null ? Arrays.asList(e.getPutStrikePrice()) : new ArrayList<>(),
                e.getPutLastPrice() != null ? Arrays.asList(e.getPutLastPrice()) : new ArrayList<>(),
                e.getPutBid() != null ? Arrays.asList(e.getPutBid()) : new ArrayList<>(),
                e.getPutAsk() != null ? Arrays.asList(e.getPutAsk()) : new ArrayList<>(),
                e.getPutVolume() != null ? Arrays.asList(e.getPutVolume()) : new ArrayList<>(),
                e.getPutOpenInterest() != null ? Arrays.asList(e.getPutOpenInterest()) : new ArrayList<>(),
                e.getPutImpliedVolatility() != null ? Arrays.asList(e.getPutImpliedVolatility()) : new ArrayList<>(),
                e.getPutDelta() != null ? Arrays.asList(e.getPutDelta()) : new ArrayList<>(),
                e.getPutGamma() != null ? Arrays.asList(e.getPutGamma()) : new ArrayList<>(),
                e.getPutTheta() != null ? Arrays.asList(e.getPutTheta()) : new ArrayList<>(),
                e.getPutVega() != null ? Arrays.asList(e.getPutVega()) : new ArrayList<>()
        );
    }





    /* ===== Helpers ===== */

    private Double parseDouble(Object val) {
        try {
            return val == null ? 0.0 : Double.valueOf(val.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Long parseLong(Object val) {
        try {
            return val == null ? 0L : Long.valueOf(val.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    private void logInfo(String message) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + message);
    }

    private void logError(String message) {
        System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + message);
    }
}
