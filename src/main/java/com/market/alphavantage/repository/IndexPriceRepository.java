package com.market.alphavantage.repository;



import com.market.alphavantage.entity.IndexPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexPriceRepository extends JpaRepository<IndexPrice, String> {
}
