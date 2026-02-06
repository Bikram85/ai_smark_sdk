package com.market.alphavantage.controller;

import com.market.alphavantage.dto.CompanyOverviewDTO;
import com.market.alphavantage.service.CompanyOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/overview")
@RequiredArgsConstructor
public class CompanyOverviewController {

    private final CompanyOverviewService service;

    @GetMapping("/load")
    public String load() {
        service.loadOverview();
        return "Overview loaded: ";
    }

    @GetMapping("/{symbol}")
    public CompanyOverviewDTO get(@PathVariable String symbol) {
        return service.getOverview(symbol);
    }
}
