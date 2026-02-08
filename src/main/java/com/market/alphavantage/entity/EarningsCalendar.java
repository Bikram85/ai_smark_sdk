package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "earnings_calendar")
@Data
public class EarningsCalendar {

    @Id
    @Column(length = 20)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "fiscal_date_ending")
    private LocalDate fiscalDateEnding;

    @Column(nullable = true) // <-- allow nulls
    private Double estimate;

    @Column(nullable = false)
    private String currency;

    @Column(name = "time_of_the_day")
    private String timeOfTheDay;
}
