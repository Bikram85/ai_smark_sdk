package com.market.alphavantage.repository;


import com.market.alphavantage.entity.CompanyOverview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyOverviewRepository
        extends JpaRepository<CompanyOverview, String> {
}

