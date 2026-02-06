package com.market.alphavantage.service;


import com.market.alphavantage.dto.SharesOutstandingDTO;

public interface SharesOutstandingService {

    void loadSharesOutstanding(String symbol);

    SharesOutstandingDTO getSharesOutstanding(String symbol);
}
