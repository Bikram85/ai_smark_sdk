package com.market.alphavantage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "top_gainers_losers")
@Getter
@Setter
public class TopGainersLosers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rowId; // unique row per symbol

    private String id; // "gainer" or "loser" — used to categorize

    @Column(nullable = false, unique = true)
    private String symbol; // unique symbol per row

    private String name;

    private Double price;

    private Double change;

    private Double percentChange;

    private Long volume;
}
