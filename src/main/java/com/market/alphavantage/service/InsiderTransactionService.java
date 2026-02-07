package com.market.alphavantage.service;



import com.market.alphavantage.dto.InsiderTransactionDTO;

public interface InsiderTransactionService {

    void loadInsiderTransactions();

    InsiderTransactionDTO getInsiderTransactions(String symbol);
}
