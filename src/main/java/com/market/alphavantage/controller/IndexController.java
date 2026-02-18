package com.market.alphavantage.controller;

import com.market.alphavantage.dto.IndexPriceDTO;

import com.market.alphavantage.service.impl.IndexPriceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IndexController {

    @Autowired
    IndexPriceServiceImpl indexPriceService;



    @GetMapping("/fetch")
    public List<IndexPriceDTO> fetchAndSave() {
         return indexPriceService.fetchAllPopularIndices();
    }

    @GetMapping("/{symbol}")
    public IndexPriceDTO getFromDB(@PathVariable String symbol) {
        return indexPriceService.getFromDB(symbol);
    }
}
