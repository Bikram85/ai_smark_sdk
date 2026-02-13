package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ETFPriceDTO {
    private String symbol;
    private List<LocalDate> tradeDates;
    private List<Double> open;
    private List<Double> high;
    private List<Double> low;
    private List<Double> close;
    private List<Double> volume;
}
