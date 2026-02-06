package com.market.alphavantage.repository;

import com.market.alphavantage.entity.IncomeStatement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeStatementRepository
        extends JpaRepository<IncomeStatement, String> {
}

