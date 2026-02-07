package com.market.alphavantage.controller;

import com.market.alphavantage.dto.DigitalCurrencyDailyDTO;
import com.market.alphavantage.service.DigitalCurrencyDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/digital-currency-daily")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DigitalCurrencyDailyController {

    private final DigitalCurrencyDailyService service;

    @PostMapping("/load")
    public ResponseEntity<String> load() {
        service.loadDigitalCurrencyDaily();
        return ResponseEntity.ok("Digital currency daily loaded for " );
    }

    @GetMapping("/get")
    public ResponseEntity<DigitalCurrencyDailyDTO> get(@RequestParam String symbol,
                                                       @RequestParam String market) {
        DigitalCurrencyDailyDTO dto = service.getDigitalCurrencyDaily(symbol, market);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
