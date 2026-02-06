package com.market.alphavantage.controller;

import com.market.alphavantage.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
