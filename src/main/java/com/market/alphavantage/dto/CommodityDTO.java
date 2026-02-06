package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommodityDTO {

    private String id;
    private String function;
    private String interval;
    private List<LocalDate> tradeDate;
    private List<Double> open;
    private List<Double> high;
    private List<Double> low;
    private List<Double> close;
    private List<Long> volume;
}
