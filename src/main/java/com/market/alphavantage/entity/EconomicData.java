package com.market.alphavantage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "economic_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EconomicData {

    @Id
    private String symbol; // e.g., REAL_GDP, CPI

    @Column(name = "indicator_name")
    private String name; // Human-readable name

    @Column(name = "interval")
    private String interval; // annual / monthly

    @Column(name = "dates")
    private LocalDate[] dates;

    @Column(name = "values")
    private Double[] values;
}
