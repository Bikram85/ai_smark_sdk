package com.market.alphavantage.controller;

import com.market.alphavantage.dto.RealtimeOptionDTO;
import com.market.alphavantage.service.RealtimeOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/realtime-options")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RealtimeOptionController {

    private final RealtimeOptionService service;

    @PostMapping("/load")
    public ResponseEntity<String> load() {
        service.loadRealtimeOptions();
        return ResponseEntity.ok("Realtime options loaded for " );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<RealtimeOptionDTO> get(@PathVariable String symbol) {
        RealtimeOptionDTO dto = service.getRealtimeOptions(symbol.toUpperCase());
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/reload/{symbol}")
    public ResponseEntity<String> reload(@PathVariable String symbol) {
        //service.loadRealtimeOptions(symbol.toUpperCase());
        return ResponseEntity.ok("Realtime options reloaded for " + symbol);
    }
}
