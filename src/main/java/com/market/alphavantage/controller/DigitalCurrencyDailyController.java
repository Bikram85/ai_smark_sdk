package com.market.alphavantage.controller;

import com.market.alphavantage.dto.DigitalCurrencyDailyDTO;
import com.market.alphavantage.service.DigitalCurrencyDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/digital-currency-daily")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DigitalCurrencyDailyController {

    private final DigitalCurrencyDailyService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadDigitalCurrencyDaily();
        return ResponseEntity.ok("Digital currency daily loaded for " );
    }

    @GetMapping("/get/{months}")
    public ResponseEntity<List<DigitalCurrencyDailyDTO>> getByMonths(
           @PathVariable int months) {

        List<DigitalCurrencyDailyDTO> dtos;

        if (months > 0) {
            dtos = service.getDigitalCurrencyByMonths(months);
        } else {
            dtos = service.getAllDigitalCurrencyDaily();
        }

        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtos);
    }
}

