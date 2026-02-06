package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "income_statement")
@Data
public class IncomeStatement {

    @Id
    private String symbol;

    /* -------- Annual Reports -------- */

    @ElementCollection
    private List<LocalDate> annualFiscalDateEnding;

    @ElementCollection
    private List<Long> annualTotalRevenue;

    @ElementCollection
    private List<Long> annualGrossProfit;

    @ElementCollection
    private List<Long> annualOperatingIncome;

    @ElementCollection
    private List<Long> annualNetIncome;

    @ElementCollection
    private List<Long> annualEbitda;

    /* -------- Quarterly Reports -------- */

    @ElementCollection
    private List<LocalDate> quarterlyFiscalDateEnding;

    @ElementCollection
    private List<Long> quarterlyTotalRevenue;

    @ElementCollection
    private List<Long> quarterlyGrossProfit;

    @ElementCollection
    private List<Long> quarterlyOperatingIncome;

    @ElementCollection
    private List<Long> quarterlyNetIncome;

    @ElementCollection
    private List<Long> quarterlyEbitda;
}
