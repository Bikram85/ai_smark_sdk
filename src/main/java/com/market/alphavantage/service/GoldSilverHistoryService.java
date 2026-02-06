package com.market.alphavantage.service;

import com.market.alphavantage.dto.GoldSilverHistoryDTO;

public interface GoldSilverHistoryService {

    void loadHistory(String symbol, String interval);

    GoldSilverHistoryDTO getHistory(String symbol, String interval);
}
