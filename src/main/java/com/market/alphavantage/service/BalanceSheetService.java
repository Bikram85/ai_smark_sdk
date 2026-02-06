package com.market.alphavantage.service;


import com.market.alphavantage.dto.BalanceSheetDTO;

public interface BalanceSheetService {

    void loadBalanceSheet(String symbol);

    BalanceSheetDTO getBalanceSheet(String symbol);
}

