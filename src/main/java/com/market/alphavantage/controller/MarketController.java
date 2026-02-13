package com.market.alphavantage.controller;

import com.market.alphavantage.dto.CommodityDTO;
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
public class MarketController {


    private final MarketService service;


    @GetMapping ("/load-symbols")
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
    public ResponseEntity<List<ETFPrice>> getByMonths(@PathVariable int months) {

        List<ETFPrice> dtos;
        dtos = service.retrieveIndexData(months); // filter by last 'months'
        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dtos);
    }
}
