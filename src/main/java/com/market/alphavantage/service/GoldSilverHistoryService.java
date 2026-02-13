package com.market.alphavantage.service;

import com.market.alphavantage.dto.GoldSilverHistoryDTO;

import java.util.List;

public interface GoldSilverHistoryService {

    void loadHistory();

    GoldSilverHistoryDTO getHistory(String symbol, String interval);

    List<GoldSilverHistoryDTO> getHistoryByMonths(int month);
}
