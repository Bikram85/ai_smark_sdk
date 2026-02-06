package com.market.alphavantage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexTechnicalIndicatorDTO {

    private String id;
    private String symbol;
    private String interval;
    private Integer timePeriod;
    private String seriesType;
    private List<LocalDate> date;
    private List<Double> sma;
}

