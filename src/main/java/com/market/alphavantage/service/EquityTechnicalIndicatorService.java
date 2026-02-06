package com.market.alphavantage.service;

import com.market.alphavantage.dto.EquityTechnicalIndicatorDTO;

public interface EquityTechnicalIndicatorService {

    void loadSMA(String symbol, String interval, Integer timePeriod, String seriesType);

    EquityTechnicalIndicatorDTO getSMA(String symbol, String interval, Integer timePeriod, String seriesType);
}
