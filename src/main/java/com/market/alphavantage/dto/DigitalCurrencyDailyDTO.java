package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalCurrencyDailyDTO {

    private String id;
    private String symbol;
    private String market;
    private List<LocalDate> tradeDate;
    private List<Double> open;
    private List<Double> high;
    private List<Double> low;
    private List<Double> close;
    private List<Double> volume;
    private List<Double> marketCap;
}
