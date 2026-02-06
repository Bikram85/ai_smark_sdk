package com.market.alphavantage.repository;

import com.market.alphavantage.entity.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashFlowRepository extends JpaRepository<CashFlow, String> {
}
