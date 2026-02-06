package com.market.alphavantage.service;

import com.market.alphavantage.entity.IncomeStatement;

public interface IncomeStatementService {

    void loadIncomeStatement();

    IncomeStatement getIncomeStatement(String symbol);
}

