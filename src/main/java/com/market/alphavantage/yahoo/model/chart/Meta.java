package com.market.alphavantage.yahoo.model.chart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meta {
    private String currency;
    private String symbol;
    private String exchangeName;
    private String fullExchangeName;
    private String instrumentType;
    private long firstTradeDate;
    private long regularMarketTime;
    private boolean hasPrePostMarketData;
    private int gmtoffset;
    private String timezone;
    private String exchangeTimezoneName;
    private double regularMarketPrice;
    private double fiftyTwoWeekHigh;
    private double fiftyTwoWeekLow;
    private double regularMarketDayHigh;
    private double regularMarketDayLow;
    private long regularMarketVolume;
    private String longName;
    private String shortName;
    private double chartPreviousClose;
    private int priceHint;
}

