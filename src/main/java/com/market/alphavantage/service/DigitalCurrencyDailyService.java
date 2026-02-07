package com.market.alphavantage.service;

import com.market.alphavantage.dto.DigitalCurrencyDailyDTO;

public interface DigitalCurrencyDailyService {

    void loadDigitalCurrencyDaily();

    DigitalCurrencyDailyDTO getDigitalCurrencyDaily(String symbol, String market);
}
