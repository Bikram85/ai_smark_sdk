package com.market.alphavantage.service;

import com.market.alphavantage.dto.GoldSilverHistoryDTO;

public interface GoldSilverHistoryService {

    void loadHistory();

    GoldSilverHistoryDTO getHistory(String symbol, String interval);
}
