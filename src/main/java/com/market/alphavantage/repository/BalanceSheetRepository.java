package com.market.alphavantage.repository;


import com.market.alphavantage.entity.BalanceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceSheetRepository
        extends JpaRepository<BalanceSheet, String> {
}
