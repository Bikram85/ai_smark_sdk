package com.market.alphavantage.controller;

import com.market.alphavantage.analytics.OptionAnalyticsService;
import com.market.alphavantage.analytics.StockSummaryService;
import com.market.alphavantage.dto.OptionDashboardDTO;
import com.market.alphavantage.dto.OptionDashboardResponseDTO;
import com.market.alphavantage.dto.StockSummaryDTO;
import com.market.alphavantage.service.OptionDashboardServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

   @Autowired
   public  OptionDashboardServiceImpl optionDashboardService;

    @GetMapping("/options/analytics")
    public void allSymbols() {
       service.analyzeAndSaveAll();
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




    @GetMapping("/{symbol}/dashboard")
    public OptionDashboardResponseDTO dashboard(
            @PathVariable String symbol) {

        return optionDashboardService.getDashboard(symbol);
    }
}
