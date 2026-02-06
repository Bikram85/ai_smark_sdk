package com.market.alphavantage.service;

import com.market.alphavantage.entity.IncomeStatement;

public interface IncomeStatementService {

    void loadIncomeStatement(String symbol);

    IncomeStatement getIncomeStatement(String symbol);
}

