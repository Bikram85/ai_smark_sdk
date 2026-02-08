package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquityTechnicalIndicatorDTO {

    private String id;
    private String symbol;
    private String interval;
    private Integer timePeriod;
    private String seriesType;
    private String function; // NEW FIELD

    private List<LocalDate> dates;
    private List<Double> values; // NEW FIELD
}
