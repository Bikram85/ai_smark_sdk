package com.market.alphavantage.service;



import com.market.alphavantage.dto.CashFlowDTO;

public interface CashFlowService {

    void loadCashFlow();

    CashFlowDTO getCashFlow(String symbol);

    void fetchDetails(String symbol);
}
