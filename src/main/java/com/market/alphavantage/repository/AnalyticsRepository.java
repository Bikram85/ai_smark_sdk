package com.market.alphavantage.repository;

import com.market.alphavantage.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository
        extends JpaRepository<Analytics, String> {
}

