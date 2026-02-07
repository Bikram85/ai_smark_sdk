package com.market.alphavantage.controller;



import com.market.alphavantage.dto.InsiderTransactionDTO;
import com.market.alphavantage.service.InsiderTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insider-transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InsiderTransactionController {

    private final InsiderTransactionService service;

    @PostMapping("/load")
    public ResponseEntity<String> load() {
        service.loadInsiderTransactions();
        return ResponseEntity.ok("Insider transactions loaded for " );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<InsiderTransactionDTO> get(@PathVariable String symbol) {
        InsiderTransactionDTO dto = service.getInsiderTransactions(symbol.toUpperCase());
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/reload/{symbol}")
    public ResponseEntity<String> reload(@PathVariable String symbol) {
        //service.loadInsiderTransactions(symbol.toUpperCase());
        return ResponseEntity.ok("Insider transactions reloaded for " + symbol);
    }
}
