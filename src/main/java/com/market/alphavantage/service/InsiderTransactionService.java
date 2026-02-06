package com.market.alphavantage.service;



import com.market.alphavantage.dto.InsiderTransactionDTO;

public interface InsiderTransactionService {

    void loadInsiderTransactions(String symbol);

    InsiderTransactionDTO getInsiderTransactions(String symbol);
}
