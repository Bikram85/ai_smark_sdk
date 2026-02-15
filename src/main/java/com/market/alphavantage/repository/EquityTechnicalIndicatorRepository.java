package com.market.alphavantage.repository;

import com.market.alphavantage.entity.ETFPrice;
import com.market.alphavantage.entity.EquityTechnicalIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquityTechnicalIndicatorRepository extends JpaRepository<EquityTechnicalIndicator, String> {

    @Query(value = "select * from equity_technical_indicator where symbol=:symbol", nativeQuery = true)
    public List<EquityTechnicalIndicator> getData(@Param("symbol") String symbol);

}
