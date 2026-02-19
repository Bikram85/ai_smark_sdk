package com.market.alphavantage.yahoo.model.chart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentTradingPeriod{
    public Pre pre;
    public Regular regular;
    public Post post;
}
