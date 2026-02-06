package com.market.alphavantage.service;



import com.market.alphavantage.dto.CashFlowDTO;

public interface CashFlowService {

    void loadCashFlow(String symbol);

    CashFlowDTO getCashFlow(String symbol);
}
