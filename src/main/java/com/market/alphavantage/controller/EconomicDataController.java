package com.market.alphavantage.controller;

import com.market.alphavantage.dto.EconomicDataDTO;
import com.market.alphavantage.service.EconomicDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/economic-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EconomicDataController {

    private final EconomicDataService service;

    // Fetch all economic data
    @GetMapping("/all")
    public ResponseEntity<List<EconomicDataDTO>> getAll() {
        List<EconomicDataDTO> allData = service.getAllEconomicData();
        return ResponseEntity.ok(allData);
    }

    // Load specific function
    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadEconomicData();
        return ResponseEntity.ok("Data loaded for " );
    }
}

