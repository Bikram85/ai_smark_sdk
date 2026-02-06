package com.market.alphavantage.service;

import com.market.alphavantage.dto.ForexTechnicalIndicatorDTO;

public interface ForexTechnicalIndicatorService {

    void loadSMA(String symbol, String interval, Integer timePeriod, String seriesType);

    ForexTechnicalIndicatorDTO getSMA(String symbol, String interval, Integer timePeriod, String seriesType);
}
