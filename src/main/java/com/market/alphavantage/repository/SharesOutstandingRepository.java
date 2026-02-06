package com.market.alphavantage.repository;


import com.market.alphavantage.entity.SharesOutstanding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharesOutstandingRepository
        extends JpaRepository<SharesOutstanding, String> {
}

