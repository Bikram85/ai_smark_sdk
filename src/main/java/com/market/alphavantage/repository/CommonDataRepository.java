package com.market.alphavantage.repository;

import com.market.alphavantage.entity.CommonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonDataRepository extends JpaRepository<CommonData, String> {
}
