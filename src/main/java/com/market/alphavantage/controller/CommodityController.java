package com.market.alphavantage.controller;

import com.market.alphavantage.dto.CommodityDTO;
import com.market.alphavantage.service.CommodityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commodities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommodityController {

    private final CommodityService service;

    @GetMapping("/load")
    public ResponseEntity<String> load() {
        service.loadCommodity();
        return ResponseEntity.ok("Commodity data loaded for ");
    }

        @GetMapping("/get/{months}")
        public ResponseEntity<List<CommodityDTO>> getByMonths(@PathVariable int months) {

            List<CommodityDTO> dtos;


            dtos = service.getCommodityByMonths(months); // filter by last 'months'


            if (dtos.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(dtos);
        }
}
