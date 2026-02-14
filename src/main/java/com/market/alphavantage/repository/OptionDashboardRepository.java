package com.market.alphavantage.repository;

import com.market.alphavantage.entity.OptionDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionDashboardRepository extends JpaRepository<OptionDashboard, Long> {

    // Find all entries by symbol
    Optional<OptionDashboard> findBySymbol(String symbol);

    Optional<OptionDashboard> findTopBySymbolOrderByIdDesc(String symbol);
}
