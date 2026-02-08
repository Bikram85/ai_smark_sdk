package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "gold_silver_history")
@Data
public class GoldSilverHistory {

    @Id
    @Column(length = 50)
    private String id; // symbol + "_" + interval

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String interval;

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

    @Column(columnDefinition = "BIGINT[]")
    private Long[] volume;
}
