package com.market.alphavantage.controller;

import com.market.alphavantage.dto.BalanceSheetDTO;
import com.market.alphavantage.service.BalanceSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance-sheet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BalanceSheetController {

    private final BalanceSheetService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadBalanceSheet();
        return ResponseEntity.ok("Balance sheet loaded for " );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<BalanceSheetDTO> get(@PathVariable String symbol) {
        BalanceSheetDTO dto = service.getBalanceSheet(symbol.toUpperCase());
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/reload/{symbol}")
    public ResponseEntity<String> reload(@PathVariable String symbol) {
        service.fetchDetails(symbol.toUpperCase());
        return ResponseEntity.ok("Balance sheet reloaded for " + symbol);
    }
}

