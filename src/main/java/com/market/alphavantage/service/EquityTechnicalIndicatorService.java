package com.market.alphavantage.service;

import com.market.alphavantage.dto.EquityTechnicalIndicatorDTO;

public interface EquityTechnicalIndicatorService {

    void loadSMA();

    EquityTechnicalIndicatorDTO getSMA(String symbol, String interval, Integer timePeriod, String seriesType);
}
