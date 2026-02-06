package com.market.alphavantage.repository;


import com.market.alphavantage.entity.BalanceSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceSheetRepository
        extends JpaRepository<BalanceSheet, String> {
}
