package com.market.alphavantage.service;

import com.market.alphavantage.dto.IncomeStatementDTO;


public interface IncomeStatementService {

    void loadIncomeStatement();

    IncomeStatementDTO getIncomeStatementDTO(String symbol);
}

