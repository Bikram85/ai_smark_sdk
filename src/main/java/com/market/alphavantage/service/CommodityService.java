package com.market.alphavantage.service;

import com.market.alphavantage.dto.CommodityDTO;

public interface CommodityService {

    void loadCommodity();

    CommodityDTO getCommodity(String function, String interval);
}
