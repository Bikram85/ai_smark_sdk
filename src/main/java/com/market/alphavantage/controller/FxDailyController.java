package com.market.alphavantage.controller;

import com.market.alphavantage.dto.CommodityDTO;
import com.market.alphavantage.dto.FxDailyDTO;
import com.market.alphavantage.service.FxDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fx-daily")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FxDailyController {

    private final FxDailyService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadFxDaily();
        return ResponseEntity.ok("FX Daily loaded for");
    }

    @GetMapping("/get")
    public ResponseEntity<FxDailyDTO> get(@RequestParam String fromSymbol,
                                          @RequestParam String toSymbol) {
        FxDailyDTO dto = service.getFxDaily(fromSymbol, toSymbol);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get/{months}")
    public ResponseEntity<List<FxDailyDTO>> getByMonths(@PathVariable int months) {

        List<FxDailyDTO> dtos;

        dtos = service.getFxDailyByMonths(months); // filter by last 'months'


        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtos);
    }
}
