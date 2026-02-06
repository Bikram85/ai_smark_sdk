package com.market.alphavantage.service;

import com.market.alphavantage.dto.FxDailyDTO;

public interface FxDailyService {

    void loadFxDaily();

    FxDailyDTO getFxDaily(String fromSymbol, String toSymbol);
}
