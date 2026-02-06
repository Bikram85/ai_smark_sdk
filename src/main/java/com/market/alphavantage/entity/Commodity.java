package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "commodities")
@Data
public class Commodity {

    @Id
    private String id; // function + "_" + interval

    private String function;
    private String interval;

    @ElementCollection
    private List<LocalDate> tradeDate;

    @ElementCollection
    private List<Double> open;

    @ElementCollection
    private List<Double> high;

    @ElementCollection
    private List<Double> low;

    @ElementCollection
    private List<Double> close;

    @ElementCollection
    private List<Long> volume;
}
