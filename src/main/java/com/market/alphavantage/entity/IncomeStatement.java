package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "income_statement")
@Getter
@Setter
public class IncomeStatement {

    @Id
    @Column(name = "symbol")
    private String symbol;

    /* ================= ANNUAL ================= */
    @Column(name = "annual_fiscal_date_ending")
    private LocalDate[] annualFiscalDateEnding;

    @Column(name = "annual_total_revenue")
    private Long[] annualTotalRevenue;

    @Column(name = "annual_cost_of_revenue")
    private Long[] annualCostOfRevenue;

    @Column(name = "annual_gross_profit")
    private Long[] annualGrossProfit;

    @Column(name = "annual_operating_expenses")
    private Long[] annualOperatingExpenses;

    @Column(name = "annual_operating_income")
    private Long[] annualOperatingIncome;

    @Column(name = "annual_ebit")
    private Long[] annualEbit;

    @Column(name = "annual_ebitda")
    private Long[] annualEbitda;

    @Column(name = "annual_interest_expense")
    private Long[] annualInterestExpense;

    @Column(name = "annual_income_before_tax")
    private Long[] annualIncomeBeforeTax;

    @Column(name = "annual_income_tax_expense")
    private Long[] annualIncomeTaxExpense;

    @Column(name = "annual_net_income")
    private Long[] annualNetIncome;

    @Column(name = "annual_net_income_from_continuing_operations")
    private Long[] annualNetIncomeFromContinuingOperations;


    /* ================= QUARTERLY ================= */
    @Column(name = "quarterly_fiscal_date_ending")
    private LocalDate[] quarterlyFiscalDateEnding;

    @Column(name = "quarterly_total_revenue")
    private Long[] quarterlyTotalRevenue;

    @Column(name = "quarterly_cost_of_revenue")
    private Long[] quarterlyCostOfRevenue;

    @Column(name = "quarterly_gross_profit")
    private Long[] quarterlyGrossProfit;

    @Column(name = "quarterly_operating_expenses")
    private Long[] quarterlyOperatingExpenses;

    @Column(name = "quarterly_operating_income")
    private Long[] quarterlyOperatingIncome;

    @Column(name = "quarterly_ebit")
    private Long[] quarterlyEbit;

    @Column(name = "quarterly_ebitda")
    private Long[] quarterlyEbitda;

    @Column(name = "quarterly_interest_expense")
    private Long[] quarterlyInterestExpense;

    @Column(name = "quarterly_income_before_tax")
    private Long[] quarterlyIncomeBeforeTax;

    @Column(name = "quarterly_income_tax_expense")
    private Long[] quarterlyIncomeTaxExpense;

    @Column(name = "quarterly_net_income")
    private Long[] quarterlyNetIncome;

    @Column(name = "quarterly_net_income_from_continuing_operations")
    private Long[] quarterlyNetIncomeFromContinuingOperations;
}
