package com.market.alphavantage.service;

import com.market.alphavantage.dto.CommodityDTO;

import java.util.List;

public interface CommodityService {

    void loadCommodity();

    List<CommodityDTO> getCommodity();

    List<CommodityDTO> getCommodityByMonths(int months);
}
