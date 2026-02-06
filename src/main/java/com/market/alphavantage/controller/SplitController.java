package com.market.alphavantage.controller;

import com.market.alphavantage.dto.SplitDTO;
import com.market.alphavantage.service.SplitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/splits")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SplitController {

    private final SplitService service;

    @PostMapping("/load/{symbol}")
    public ResponseEntity<String> load(@PathVariable String symbol) {
        service.loadSplits(symbol);
        return ResponseEntity.ok("Splits loaded for " + symbol);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<SplitDTO> get(@PathVariable String symbol) {
        SplitDTO dto = service.getSplits(symbol);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
