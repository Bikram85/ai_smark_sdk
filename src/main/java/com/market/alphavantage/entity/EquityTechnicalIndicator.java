package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "equity_technical_indicator")
@Data
public class EquityTechnicalIndicator {

    @Id
    @Column(length = 100)
    private String id; // symbol + "_" + interval + "_" + timePeriod + "_" + seriesType + "_" + function

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String interval;

    @Column(nullable = false)
    private Integer timePeriod;

    @Column(nullable = false)
    private String seriesType;

    @Column(nullable = false)
    private String function; // NEW: SMA, RSI, MACD, etc.

    // Store dates as LocalDate array
    @Column(columnDefinition = "date[]")
    private LocalDate[] dates;

    // Generic numeric values for the function
    @Column(columnDefinition = "double precision[]")
    private Double[] values; // NEW: replace smaValues with generic
}
