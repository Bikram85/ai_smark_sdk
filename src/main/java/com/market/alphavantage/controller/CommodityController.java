package com.market.alphavantage.controller;

import com.market.alphavantage.dto.CommodityDTO;
import com.market.alphavantage.service.CommodityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commodities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommodityController {

    private final CommodityService service;

    @PostMapping("/load")
    public ResponseEntity<String> load(@RequestParam String function,
                                       @RequestParam String interval) {
        service.loadCommodity(function, interval);
        return ResponseEntity.ok("Commodity data loaded for " + function + " [" + interval + "]");
    }

    @GetMapping("/get")
    public ResponseEntity<CommodityDTO> get(@RequestParam String function,
                                            @RequestParam String interval) {
        CommodityDTO dto = service.getCommodity(function, interval);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
