package com.market.alphavantage.repository;



import com.market.alphavantage.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {}
