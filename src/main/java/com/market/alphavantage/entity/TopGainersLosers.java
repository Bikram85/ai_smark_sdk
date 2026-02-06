package com.market.alphavantage.entity;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "top_gainers_losers")
@Data
public class TopGainersLosers {

    @Id
    private String id; // e.g. "top_gainers" or "top_losers"


    private String type;
    @ElementCollection
    private List<String> symbol;

    @ElementCollection
    private List<String> name;

    @ElementCollection
    private List<Double> price;

    @ElementCollection
    private List<Double> change;

    @ElementCollection
    private List<Double> percentChange;

    @ElementCollection
    private List<Long> volume;
}
