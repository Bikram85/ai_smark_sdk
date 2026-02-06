package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "forex_technical_indicator")
@Data
public class ForexTechnicalIndicator {

    @Id
    private String id; // symbol + "_" + interval + "_" + timePeriod + "_" + seriesType

    private String symbol;
    private String interval;
    private Integer timePeriod;
    private String seriesType;

    @ElementCollection
    private List<LocalDate> date;

    @ElementCollection
    private List<Double> sma;
}
