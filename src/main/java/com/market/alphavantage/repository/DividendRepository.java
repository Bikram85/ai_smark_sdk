package com.market.alphavantage.repository;

import com.market.alphavantage.entity.Dividend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividendRepository extends JpaRepository<Dividend, String> {}
