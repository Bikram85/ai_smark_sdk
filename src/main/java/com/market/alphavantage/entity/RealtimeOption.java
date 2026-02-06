package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "realtime_options")
@Data
public class RealtimeOption {

    @Id
    private String symbol;

    @ElementCollection
    private List<String> expirationDate;

    @ElementCollection
    private List<String> optionType; // "call" or "put"

    @ElementCollection
    private List<Double> strikePrice;

    @ElementCollection
    private List<Double> lastPrice;

    @ElementCollection
    private List<Double> bid;

    @ElementCollection
    private List<Double> ask;

    @ElementCollection
    private List<Long> volume;

    @ElementCollection
    private List<Long> openInterest;

    @ElementCollection
    private List<Double> impliedVolatility;

    @ElementCollection
    private List<Double> delta;

    @ElementCollection
    private List<Double> gamma;

    @ElementCollection
    private List<Double> theta;

    @ElementCollection
    private List<Double> vega;
}
