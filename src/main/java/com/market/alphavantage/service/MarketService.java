package com.market.alphavantage.service;

import com.market.alphavantage.entity.ETFPrice;
import com.market.alphavantage.entity.StockPrice;

import java.util.List;

public interface MarketService {
    void loadListingStatus();
    void loadDailyPrices();
    List<ETFPrice> retrieveIndexData(int months);
}
