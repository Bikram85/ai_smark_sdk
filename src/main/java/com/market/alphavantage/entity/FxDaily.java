package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "fx_daily")
@Data
public class FxDaily {

    @Id
    private String id; // combination of fromSymbol + "_" + toSymbol

    private String fromSymbol;
    private String toSymbol;

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
}
