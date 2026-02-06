package com.market.alphavantage.controller;


import com.market.alphavantage.entity.IncomeStatement;
import com.market.alphavantage.service.IncomeStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income-statement")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IncomeStatementController {

    private final IncomeStatementService service;

    /**
     * Load data from Alpha Vantage and save
     */
    @PostMapping("/load/{symbol}")
    public ResponseEntity<String> loadIncomeStatement(
            @PathVariable String symbol) {

        service.loadIncomeStatement(symbol.toUpperCase());

        return ResponseEntity.ok(
                "Income statement loaded for " + symbol
        );
    }

    /**
     * Get saved income statement
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<IncomeStatement> getIncomeStatement(
            @PathVariable String symbol) {

        IncomeStatement data =
                service.getIncomeStatement(symbol.toUpperCase());

        if (data == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(data);
    }

    /**
     * Reload symbol data
     */
    @PutMapping("/reload/{symbol}")
    public ResponseEntity<String> reloadIncomeStatement(
            @PathVariable String symbol) {

        service.loadIncomeStatement(symbol.toUpperCase());

        return ResponseEntity.ok(
                "Income statement refreshed for " + symbol
        );
    }
}
