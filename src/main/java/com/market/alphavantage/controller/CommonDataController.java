package com.market.alphavantage.controller;


import com.market.alphavantage.dto.CompanyOverviewDTO;
import com.market.alphavantage.service.CompanyOverviewService;
import com.market.alphavantage.service.impl.CommonDataServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class CommonDataController {

    private final CommonDataServiceImpl service;

    @GetMapping("/load")
    public String load() {
        service.fetchAllPopularIndices();
        return "Overview loaded: ";
    }


}