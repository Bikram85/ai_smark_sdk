package com.market.alphavantage.controller;

import com.market.alphavantage.dto.GoldSilverHistoryDTO;
import com.market.alphavantage.service.GoldSilverHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gold-silver-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoldSilverHistoryController {

    private final GoldSilverHistoryService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadHistory();
        return ResponseEntity.ok("Gold/Silver history loaded for ");
    }

    @GetMapping("/get")
    public ResponseEntity<GoldSilverHistoryDTO> get(@RequestParam String symbol,
                                                    @RequestParam String interval) {
        GoldSilverHistoryDTO dto = service.getHistory(symbol, interval);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
