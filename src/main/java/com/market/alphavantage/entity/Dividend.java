package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dividends")
@Data
public class Dividend {

    @Id
    private String symbol;

    @ElementCollection
    private List<LocalDate> exDividendDate;

    @ElementCollection
    private List<Double> dividendAmount;
}
