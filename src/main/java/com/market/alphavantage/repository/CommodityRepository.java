package com.market.alphavantage.repository;

import com.market.alphavantage.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommodityRepository extends JpaRepository<Commodity, String> {}
