package com.market.alphavantage.service;

import com.market.alphavantage.dto.EconomicDataDTO;

import java.util.List;

public interface EconomicDataService {
    void loadEconomicData();
    List<EconomicDataDTO> getAllEconomicData();
}
