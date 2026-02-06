package com.market.alphavantage.controller;

import com.market.alphavantage.dto.DividendDTO;
import com.market.alphavantage.service.DividendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dividends")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DividendController {

    private final DividendService service;

    @PostMapping("/load/{symbol}")
    public ResponseEntity<String> load(@PathVariable String symbol) {
        service.loadDividends(symbol);
        return ResponseEntity.ok("Dividends loaded for " + symbol);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<DividendDTO> get(@PathVariable String symbol) {
        DividendDTO dto = service.getDividends(symbol);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
