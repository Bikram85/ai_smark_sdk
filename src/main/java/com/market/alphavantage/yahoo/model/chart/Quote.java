package com.market.alphavantage.yahoo.model.chart;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote{
    private List<Double> open;
    private List<Double> close;
    private List<Double> high;
    private List<Double> low;
    private List<Long> volume;
}
