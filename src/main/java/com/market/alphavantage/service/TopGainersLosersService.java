package com.market.alphavantage.service;

import com.market.alphavantage.dto.TopGainersLosersDTO;

public interface TopGainersLosersService {

    void loadTopGainersLosers();

    TopGainersLosersDTO getTopGainersLosers(String id);
}
