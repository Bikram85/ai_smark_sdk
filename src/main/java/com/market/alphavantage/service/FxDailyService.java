package com.market.alphavantage.service;

import com.market.alphavantage.dto.FxDailyDTO;

import java.util.List;

public interface FxDailyService {

    void loadFxDaily();

    FxDailyDTO getFxDaily(String fromSymbol, String toSymbol);
    List<FxDailyDTO> getFxDailyByMonths(int months);
}
