package com.market.alphavantage.repository;

import com.market.alphavantage.entity.EquityTechnicalIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquityTechnicalIndicatorRepository extends JpaRepository<EquityTechnicalIndicator, String> {}
