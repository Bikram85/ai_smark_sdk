package com.market.alphavantage.repository;

import com.market.alphavantage.entity.TopGainersLosers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopGainersLosersRepository extends JpaRepository<TopGainersLosers, String> {}
