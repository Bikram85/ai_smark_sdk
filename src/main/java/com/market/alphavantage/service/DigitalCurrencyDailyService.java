package com.market.alphavantage.service;

import com.market.alphavantage.dto.DigitalCurrencyDailyDTO;

import java.util.List;

public interface DigitalCurrencyDailyService {

    void loadDigitalCurrencyDaily();
    List<DigitalCurrencyDailyDTO> getDigitalCurrencyByMonths(int months);

    List<DigitalCurrencyDailyDTO> getAllDigitalCurrencyDaily();
}
