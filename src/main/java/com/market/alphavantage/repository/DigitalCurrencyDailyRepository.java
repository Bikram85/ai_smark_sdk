package com.market.alphavantage.repository;

import com.market.alphavantage.entity.DigitalCurrencyDaily;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DigitalCurrencyDailyRepository extends JpaRepository<DigitalCurrencyDaily, String> {}
