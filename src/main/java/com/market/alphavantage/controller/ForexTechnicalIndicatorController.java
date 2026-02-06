package com.market.alphavantage.controller;

import com.market.alphavantage.dto.ForexTechnicalIndicatorDTO;
import com.market.alphavantage.service.ForexTechnicalIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forex-technical-indicator")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ForexTechnicalIndicatorController {

    private final ForexTechnicalIndicatorService service;

    @PostMapping("/load")
    public ResponseEntity<String> load(@RequestParam String symbol,
                                       @RequestParam String interval,
                                       @RequestParam Integer timePeriod,
                                       @RequestParam String seriesType) {
        service.loadSMA(symbol, interval, timePeriod, seriesType);
        return ResponseEntity.ok("Forex SMA loaded for " + symbol + " [" + interval + "]");
    }

    @GetMapping("/get")
    public ResponseEntity<ForexTechnicalIndicatorDTO> get(@RequestParam String symbol,
                                                          @RequestParam String interval,
                                                          @RequestParam Integer timePeriod,
                                                          @RequestParam String seriesType) {
        ForexTechnicalIndicatorDTO dto = service.getSMA(symbol, interval, timePeriod, seriesType);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
