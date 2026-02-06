package com.market.alphavantage.repository;

import com.market.alphavantage.entity.FxDaily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FxDailyRepository extends JpaRepository<FxDaily, String> {}
