package com.market.alphavantage.controller;

import com.market.alphavantage.dto.TopGainersLosersDTO;
import com.market.alphavantage.service.TopGainersLosersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/top-gainers-losers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TopGainersLosersController {

    private final TopGainersLosersService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadTopGainersLosers();
        return ResponseEntity.ok("Top gainers and losers loaded");
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopGainersLosersDTO> get(@PathVariable String id) {
        TopGainersLosersDTO dto = service.getTopGainersLosers(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}

