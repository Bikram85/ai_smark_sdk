package com.market.alphavantage.repository;



import com.market.alphavantage.entity.ETFPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ETFPriceRepository extends JpaRepository<ETFPrice, Long> {}
