package com.market.alphavantage.service;

import com.market.alphavantage.dto.DividendDTO;

public interface DividendService {

    void loadDividends();

    DividendDTO getDividends(String symbol);
}
