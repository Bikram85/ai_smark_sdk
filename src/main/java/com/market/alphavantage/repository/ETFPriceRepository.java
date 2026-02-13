package com.market.alphavantage.repository;



import com.market.alphavantage.entity.ETFPrice;
import com.market.alphavantage.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ETFPriceRepository extends JpaRepository<ETFPrice, Long> {

    @Query(value = "select * from etf_price where symbol in ('SPY','QQQ')", nativeQuery = true)
    public List<ETFPrice> getIndexData();
}
