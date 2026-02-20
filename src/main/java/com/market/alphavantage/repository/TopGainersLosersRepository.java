package com.market.alphavantage.repository;

import com.market.alphavantage.entity.TopGainersLosers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopGainersLosersRepository extends JpaRepository<TopGainersLosers, Long> {
    List<TopGainersLosers> findById(String id);
    Optional<TopGainersLosers> findBySymbol(String symbol);// fetch all rows for "gainer" or "loser"
}
