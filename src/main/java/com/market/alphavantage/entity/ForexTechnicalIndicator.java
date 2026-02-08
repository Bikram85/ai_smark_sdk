package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "forex_technical_indicator")
@Data
public class ForexTechnicalIndicator {

    @Id
    @Column(length = 100)
    private String id; // e.g., symbol + "_" + interval + "_" + timePeriod + "_" + seriesType

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String interval;

    @Column
    private Integer timePeriod; // nullable for indicators without a time period

    @Column(nullable = false)
    private String seriesType; // e.g., "close", "open", etc.

    // Store all dates as array
    @Column(columnDefinition = "DATE[]")
    private LocalDate[] dates;

    // Store all SMA values as array
    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] smaValues;
}
