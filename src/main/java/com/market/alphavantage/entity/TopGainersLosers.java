package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "top_gainers_losers")
@Data
public class TopGainersLosers {

    @Id
    private String id; // e.g. "top_gainers" or "top_losers"

    private String type;

    @Column(columnDefinition = "TEXT")
    private String[] symbol;

    @Column(columnDefinition = "TEXT")
    private String[] name;

    @Column(columnDefinition = "TEXT")
    private Double[] price;

    @Column(columnDefinition = "TEXT")
    private Double[] change;

    @Column(columnDefinition = "TEXT")
    private Double[] percentChange;

    @Column(columnDefinition = "TEXT")
    private Long[] volume;
}
