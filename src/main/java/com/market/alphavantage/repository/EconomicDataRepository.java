package com.market.alphavantage.repository;

import com.market.alphavantage.entity.EconomicData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EconomicDataRepository extends JpaRepository<EconomicData, String> {
}
