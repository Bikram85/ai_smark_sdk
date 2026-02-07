package com.market.alphavantage.service;


import com.market.alphavantage.dto.SharesOutstandingDTO;

public interface SharesOutstandingService {

    void loadSharesOutstanding();

    SharesOutstandingDTO getSharesOutstanding(String symbol);
}
