package com.market.alphavantage.service;


import com.market.alphavantage.dto.CompanyOverviewDTO;

public interface CompanyOverviewService {

    void loadOverview(String symbol);

    CompanyOverviewDTO getOverview(String symbol);
}
