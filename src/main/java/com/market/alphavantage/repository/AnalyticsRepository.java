package com.market.alphavantage.repository;

import com.market.alphavantage.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository
        extends JpaRepository<Analytics, String> {

    @Query("SELECT a.symbol FROM Analytics a " +
            "WHERE a.avgVolumeWeek > :volume AND a.marketCap > 300000000")
    List<String> findSymbolsWithAvgVolumeAndMarketCap(@Param("volume") Double volume);
}

