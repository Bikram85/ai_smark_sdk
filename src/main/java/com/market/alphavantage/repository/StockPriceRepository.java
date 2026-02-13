package com.market.alphavantage.repository;



import com.market.alphavantage.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {



}
