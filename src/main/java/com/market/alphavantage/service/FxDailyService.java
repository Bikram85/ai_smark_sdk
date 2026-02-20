package com.market.alphavantage.service;

import com.market.alphavantage.dto.FxDailyDTO;

import java.util.List;

public interface FxDailyService {

    void loadFxDaily();
    void loadFxIntraday();
    List<FxDailyDTO> getFxDailyByMonths();
}
