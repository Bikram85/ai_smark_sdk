package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FxDailyDTO {

    private String id;
    private String fromSymbol;
    private String toSymbol;
    private List<LocalDate> tradeDate;
    private List<Double> open;
    private List<Double> high;
    private List<Double> low;
    private List<Double> close;
}
