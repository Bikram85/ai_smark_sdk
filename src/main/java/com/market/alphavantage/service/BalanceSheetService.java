package com.market.alphavantage.service;


import com.market.alphavantage.dto.BalanceSheetDTO;

public interface BalanceSheetService {

    void loadBalanceSheet();

    BalanceSheetDTO getBalanceSheet(String symbol);

}

