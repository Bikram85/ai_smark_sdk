package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeOptionDTO {

    private String symbol;

    /* ===== CALLS ===== */
    private List<String> callExpirationDate;
    private List<Double> callStrikePrice;
    private List<Double> callLastPrice;
    private List<Double> callBid;
    private List<Double> callAsk;
    private List<Long> callVolume;
    private List<Long> callOpenInterest;
    private List<Double> callImpliedVolatility;
    private List<Double> callDelta;
    private List<Double> callGamma;
    private List<Double> callTheta;
    private List<Double> callVega;

    /* ===== PUTS ===== */
    private List<String> putExpirationDate;
    private List<Double> putStrikePrice;
    private List<Double> putLastPrice;
    private List<Double> putBid;
    private List<Double> putAsk;
    private List<Long> putVolume;
    private List<Long> putOpenInterest;
    private List<Double> putImpliedVolatility;
    private List<Double> putDelta;
    private List<Double> putGamma;
    private List<Double> putTheta;
    private List<Double> putVega;
}
