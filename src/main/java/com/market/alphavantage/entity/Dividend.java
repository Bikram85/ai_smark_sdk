package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "dividends")
@Data
public class Dividend {

    @Id
    @Column(name = "symbol")
    private String symbol;

    // PostgreSQL arrays
    @Column(name = "ex_dividend_dates", columnDefinition = "date[]")
    private LocalDate[] exDividendDates;

    @Column(name = "dividend_amounts", columnDefinition = "double precision[]")
    private Double[] dividendAmounts;
}
