package com.market.alphavantage.controller;

import com.market.alphavantage.dto.FxDailyDTO;
import com.market.alphavantage.service.FxDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fx-daily")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FxDailyController {

    private final FxDailyService service;

    @PostMapping("/load")
    public ResponseEntity<String> load(@RequestParam String fromSymbol,
                                       @RequestParam String toSymbol) {
        service.loadFxDaily(fromSymbol, toSymbol);
        return ResponseEntity.ok("FX Daily loaded for " + fromSymbol + " -> " + toSymbol);
    }

    @GetMapping("/get")
    public ResponseEntity<FxDailyDTO> get(@RequestParam String fromSymbol,
                                          @RequestParam String toSymbol) {
        FxDailyDTO dto = service.getFxDaily(fromSymbol, toSymbol);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
