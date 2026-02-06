package com.market.alphavantage.entity;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ipo_calendar")
@Data
public class IpoCalendar {

    @Id
    private String id;  // Example id: "ipo_calendar_all"

    @ElementCollection
    private List<String> symbol;

    @ElementCollection
    private List<String> name;

    @ElementCollection
    private List<LocalDate> ipoDate;

    @ElementCollection
    private List<String> price;

    @ElementCollection
    private List<String> shares;

    @ElementCollection
    private List<String> exchange;

    @ElementCollection
    private List<String> currency;
}
