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

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadCommodity("WTI", "daily");
        service.loadCommodity("BRENT", "daily");
        service.loadCommodity("NATURAL_GAS", "daily");
        service.loadCommodity("COPPER", "daily");
        service.loadCommodity("ALUMINUM", "daily");
        service.loadCommodity("WHEAT", "daily");
        service.loadCommodity("CORN", "daily");
        service.loadCommodity("COTTON", "daily");
        service.loadCommodity("SUGAR", "daily");
        service.loadCommodity("COFFEE", "daily");
        service.loadCommodity("ALL_COMMODITIES", "daily");
        return ResponseEntity.ok("Commodity data loaded for ");
    }

    @GetMapping("/get")
    public ResponseEntity<CommodityDTO> get(@RequestParam String function,
                                            @RequestParam String interval) {
        CommodityDTO dto = service.getCommodity(function, interval);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }
}
