package com.market.alphavantage.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "stk_price")
@Data
public class StockPrice {

    @Id
    private String symbol;

    @Column(name = "trade_dates")
    private LocalDate[] tradeDates;

    private Double[] open;

    private Double[] high;

    private Double[] low;

    private Double[] close;

    private Long[] volume;
}

