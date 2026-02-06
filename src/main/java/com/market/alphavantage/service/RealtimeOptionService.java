package com.market.alphavantage.service;

import com.market.alphavantage.dto.RealtimeOptionDTO;

public interface RealtimeOptionService {

    void loadRealtimeOptions(String symbol);

    RealtimeOptionDTO getRealtimeOptions(String symbol);
}
