package com.market.alphavantage.service;

import com.market.alphavantage.dto.DividendDTO;

public interface DividendService {

    void loadDividends(String symbol);

    DividendDTO getDividends(String symbol);
}
