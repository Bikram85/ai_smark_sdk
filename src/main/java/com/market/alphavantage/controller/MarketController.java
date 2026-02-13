package com.market.alphavantage.controller;

import com.market.alphavantage.dto.CommodityDTO;
import com.market.alphavantage.dto.ETFPriceDTO;
import com.market.alphavantage.entity.ETFPrice;
import com.market.alphavantage.entity.StockPrice;
import com.market.alphavantage.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MarketController {


    private final MarketService service;


    @GetMapping("/load-symbols")
    public String loadSymbols() {
        service.loadListingStatus();
        return "Symbols loaded";
    }


    @GetMapping("/load-prices")
    public String loadPrices() {
        service.loadDailyPrices();
        return "Prices loaded";
    }

    @GetMapping("/get/{months}")
    public ResponseEntity< List<ETFPriceDTO>> getByMonths(@PathVariable int months) {

        List<ETFPriceDTO> dtos = service.retrieveIndexData(months);
        if (dtos == null || dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtos);
    }
}
