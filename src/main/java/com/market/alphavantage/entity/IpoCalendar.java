package com.market.alphavantage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "ipo_calendar")
@Data
public class IpoCalendar {

    @Id
    private String symbol;  // use symbol as primary key (unique per row)

    private String name;
    private LocalDate ipoDate;
    private String price;
    private String shares;
    private String exchange;
    private String currency;
}
