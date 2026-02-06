package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "earnings_calendar")
@Data
public class EarningsCalendar {

    @Id
    private String id;  // e.g. "horizon_3month" or symbol+horizon

    @ElementCollection
    private List<String> symbol;

    @ElementCollection
    private List<String> name;

    @ElementCollection
    private List<LocalDate> reportDate;

    @ElementCollection
    private List<LocalDate> fiscalDateEnding;

    @ElementCollection
    private List<Double> estimate;

    @ElementCollection
    private List<String> currency;

    @ElementCollection
    private List<String> timeOfTheDay;
}
