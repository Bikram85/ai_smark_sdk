package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "digital_currency_daily")
@Data
public class DigitalCurrencyDaily {

    @Id
    @Column(length = 50)
    private String id; // combination: symbol + "_" + market

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String market;

    // Store all values as arrays for single-row storage
    @Column(columnDefinition = "DATE[]")
    private LocalDate[] tradeDate;

    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] open;

    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] high;

    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] low;

    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] close;

    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] volume;

    @Column(columnDefinition = "DOUBLE PRECISION[]")
    private Double[] marketCap;
}
