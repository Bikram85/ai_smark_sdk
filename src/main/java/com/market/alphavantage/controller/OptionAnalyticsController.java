package com.market.alphavantage.controller;

import com.market.alphavantage.analytics.OptionAnalyticsService;
import com.market.alphavantage.analytics.StockSummaryService;
import com.market.alphavantage.dto.OptionDashboardDTO;
import com.market.alphavantage.dto.StockSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OptionAnalyticsController {

    private final OptionAnalyticsService service;
    private final StockSummaryService stockSummaryService;

    @GetMapping("/dashboard/all")
    public List<OptionDashboardDTO> allSymbols() {
        return service.analyzeAll();
    }

    @GetMapping("/{symbol}/summary")
    public ResponseEntity<?> getStockSummary(@PathVariable String symbol) {
        try {
            StockSummaryDTO summary = stockSummaryService.getStockSummary(symbol);

            if (summary == null) {
                return ResponseEntity
                        .status(404)
                        .body("No data found for symbol: " + symbol);
            }

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body("Error fetching stock summary for symbol: " + symbol);
        }
    }
}
