package com.market.alphavantage.controller;



import com.market.alphavantage.dto.EquityTechnicalIndicatorDTO;
import com.market.alphavantage.service.EquityTechnicalIndicatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equity-technical-indicator")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EquityTechnicalIndicatorController {

    private final EquityTechnicalIndicatorService service;

    @PostMapping("/load")
    public ResponseEntity<String> load(@RequestParam String symbol,
                                       @RequestParam String interval,
                                       @RequestParam Integer timePeriod,
                                       @RequestParam String seriesType) {
        service.loadSMA(symbol, interval, timePeriod, seriesType);
        return ResponseEntity.ok("SMA loaded for " + symbol + " [" + interval + "]");
    }

    @GetMapping("/get")
    public ResponseEntity<EquityTechnicalIndicatorDTO> get(@RequestParam String symbol,
                                                           @RequestParam String interval,
                                                           @RequestParam Integer timePeriod,
                                                           @RequestParam String seriesType) {
        EquityTechnicalIndicatorDTO dto = service.getSMA(symbol, interval, timePeriod, seriesType);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
