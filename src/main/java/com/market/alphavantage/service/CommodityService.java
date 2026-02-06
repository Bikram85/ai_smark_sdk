package com.market.alphavantage.service;

import com.market.alphavantage.dto.CommodityDTO;

public interface CommodityService {

    void loadCommodity(String function, String interval);

    CommodityDTO getCommodity(String function, String interval);
}
