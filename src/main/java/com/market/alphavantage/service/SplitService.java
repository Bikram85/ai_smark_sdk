package com.market.alphavantage.service;

import com.market.alphavantage.dto.SplitDTO;

public interface SplitService {

    void loadSplits(String symbol);

    SplitDTO getSplits(String symbol);
}
