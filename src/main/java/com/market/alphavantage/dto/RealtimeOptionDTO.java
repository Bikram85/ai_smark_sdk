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

    private List<String> expirationDate;
    private List<String> optionType;
    private List<Double> strikePrice;
    private List<Double> lastPrice;
    private List<Double> bid;
    private List<Double> ask;
    private List<Long> volume;
    private List<Long> openInterest;
    private List<Double> impliedVolatility;
    private List<Double> delta;
    private List<Double> gamma;
    private List<Double> theta;
    private List<Double> vega;
}
