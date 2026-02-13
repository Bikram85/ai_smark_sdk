package com.market.alphavantage.controller;

import com.market.alphavantage.dto.FxDailyDTO;
import com.market.alphavantage.dto.GoldSilverHistoryDTO;
import com.market.alphavantage.service.GoldSilverHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get/{months}")
    public ResponseEntity<List<GoldSilverHistoryDTO>> getByMonths(@PathVariable int months) {

        List<GoldSilverHistoryDTO> dtos;

        dtos = service.getHistoryByMonths(months); // filter by last 'months'


        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtos);
    }
}
