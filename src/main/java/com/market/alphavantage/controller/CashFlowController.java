package com.market.alphavantage.controller;



import com.market.alphavantage.dto.CashFlowDTO;
import com.market.alphavantage.service.CashFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-flow")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CashFlowController {

    private final CashFlowService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadCashFlow();
        return ResponseEntity.ok("Cash Flow loaded for " );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<CashFlowDTO> get(@PathVariable String symbol) {
        CashFlowDTO dto = service.getCashFlow(symbol.toUpperCase());
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/reload/{symbol}")
    public ResponseEntity<String> reload(@PathVariable String symbol) {
        service.fetchDetails(symbol.toUpperCase());
        return ResponseEntity.ok("Cash Flow reloaded for " + symbol);
    }
}

