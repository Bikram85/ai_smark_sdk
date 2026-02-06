package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "digital_currency_daily")
@Data
public class DigitalCurrencyDaily {

    @Id
    private String id; // combination symbol + "_" + market

    private String symbol;
    private String market;

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
    private List<Double> volume;

    @ElementCollection
    private List<Double> marketCap;
}
