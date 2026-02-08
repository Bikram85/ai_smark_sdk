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

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadDividends();
        return ResponseEntity.ok("Dividends loaded for " );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<DividendDTO> get(@PathVariable String symbol) {
        DividendDTO dto = service.getDividends(symbol);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
