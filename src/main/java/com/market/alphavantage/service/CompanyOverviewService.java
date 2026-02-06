package com.market.alphavantage.service;


import com.market.alphavantage.dto.CompanyOverviewDTO;

public interface CompanyOverviewService {

    void loadOverview();

    CompanyOverviewDTO getOverview(String symbol);
}
