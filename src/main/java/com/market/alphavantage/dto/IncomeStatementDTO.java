package com.market.alphavantage.dto;

import com.market.alphavantage.entity.IncomeStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class IncomeStatementDTO {

    private String symbol;

    /* ================= ANNUAL ================= */
    private List<LocalDate> annualFiscalDateEnding;
    private List<Long> annualTotalRevenue;
    private List<Long> annualCostOfRevenue;
    private List<Long> annualGrossProfit;
    private List<Long> annualOperatingExpenses;
    private List<Long> annualOperatingIncome;
    private List<Long> annualEbit;
    private List<Long> annualEbitda;
    private List<Long> annualInterestExpense;
    private List<Long> annualIncomeBeforeTax;
    private List<Long> annualIncomeTaxExpense;
    private List<Long> annualNetIncome;
    private List<Long> annualNetIncomeFromContinuingOperations;

    /* ================= QUARTERLY ================= */
    private List<LocalDate> quarterlyFiscalDateEnding;
    private List<Long> quarterlyTotalRevenue;
    private List<Long> quarterlyCostOfRevenue;
    private List<Long> quarterlyGrossProfit;
    private List<Long> quarterlyOperatingExpenses;
    private List<Long> quarterlyOperatingIncome;
    private List<Long> quarterlyEbit;
    private List<Long> quarterlyEbitda;
    private List<Long> quarterlyInterestExpense;
    private List<Long> quarterlyIncomeBeforeTax;
    private List<Long> quarterlyIncomeTaxExpense;
    private List<Long> quarterlyNetIncome;
    private List<Long> quarterlyNetIncomeFromContinuingOperations;

    /* ===== Constructor from IncomeStatement entity ===== */
    public IncomeStatementDTO(IncomeStatement entity) {
        this.symbol = entity.getSymbol();

        // Annual
        this.annualFiscalDateEnding = entity.getAnnualFiscalDateEnding() != null ?
                Arrays.asList(entity.getAnnualFiscalDateEnding()) : Collections.emptyList();
        this.annualTotalRevenue = entity.getAnnualTotalRevenue() != null ?
                Arrays.asList(entity.getAnnualTotalRevenue()) : Collections.emptyList();
        this.annualCostOfRevenue = entity.getAnnualCostOfRevenue() != null ?
                Arrays.asList(entity.getAnnualCostOfRevenue()) : Collections.emptyList();
        this.annualGrossProfit = entity.getAnnualGrossProfit() != null ?
                Arrays.asList(entity.getAnnualGrossProfit()) : Collections.emptyList();
        this.annualOperatingExpenses = entity.getAnnualOperatingExpenses() != null ?
                Arrays.asList(entity.getAnnualOperatingExpenses()) : Collections.emptyList();
        this.annualOperatingIncome = entity.getAnnualOperatingIncome() != null ?
                Arrays.asList(entity.getAnnualOperatingIncome()) : Collections.emptyList();
        this.annualEbit = entity.getAnnualEbit() != null ?
                Arrays.asList(entity.getAnnualEbit()) : Collections.emptyList();
        this.annualEbitda = entity.getAnnualEbitda() != null ?
                Arrays.asList(entity.getAnnualEbitda()) : Collections.emptyList();
        this.annualInterestExpense = entity.getAnnualInterestExpense() != null ?
                Arrays.asList(entity.getAnnualInterestExpense()) : Collections.emptyList();
        this.annualIncomeBeforeTax = entity.getAnnualIncomeBeforeTax() != null ?
                Arrays.asList(entity.getAnnualIncomeBeforeTax()) : Collections.emptyList();
        this.annualIncomeTaxExpense = entity.getAnnualIncomeTaxExpense() != null ?
                Arrays.asList(entity.getAnnualIncomeTaxExpense()) : Collections.emptyList();
        this.annualNetIncome = entity.getAnnualNetIncome() != null ?
                Arrays.asList(entity.getAnnualNetIncome()) : Collections.emptyList();
        this.annualNetIncomeFromContinuingOperations = entity.getAnnualNetIncomeFromContinuingOperations() != null ?
                Arrays.asList(entity.getAnnualNetIncomeFromContinuingOperations()) : Collections.emptyList();

        // Quarterly
        this.quarterlyFiscalDateEnding = entity.getQuarterlyFiscalDateEnding() != null ?
                Arrays.asList(entity.getQuarterlyFiscalDateEnding()) : Collections.emptyList();
        this.quarterlyTotalRevenue = entity.getQuarterlyTotalRevenue() != null ?
                Arrays.asList(entity.getQuarterlyTotalRevenue()) : Collections.emptyList();
        this.quarterlyCostOfRevenue = entity.getQuarterlyCostOfRevenue() != null ?
                Arrays.asList(entity.getQuarterlyCostOfRevenue()) : Collections.emptyList();
        this.quarterlyGrossProfit = entity.getQuarterlyGrossProfit() != null ?
                Arrays.asList(entity.getQuarterlyGrossProfit()) : Collections.emptyList();
        this.quarterlyOperatingExpenses = entity.getQuarterlyOperatingExpenses() != null ?
                Arrays.asList(entity.getQuarterlyOperatingExpenses()) : Collections.emptyList();
        this.quarterlyOperatingIncome = entity.getQuarterlyOperatingIncome() != null ?
                Arrays.asList(entity.getQuarterlyOperatingIncome()) : Collections.emptyList();
        this.quarterlyEbit = entity.getQuarterlyEbit() != null ?
                Arrays.asList(entity.getQuarterlyEbit()) : Collections.emptyList();
        this.quarterlyEbitda = entity.getQuarterlyEbitda() != null ?
                Arrays.asList(entity.getQuarterlyEbitda()) : Collections.emptyList();
        this.quarterlyInterestExpense = entity.getQuarterlyInterestExpense() != null ?
                Arrays.asList(entity.getQuarterlyInterestExpense()) : Collections.emptyList();
        this.quarterlyIncomeBeforeTax = entity.getQuarterlyIncomeBeforeTax() != null ?
                Arrays.asList(entity.getQuarterlyIncomeBeforeTax()) : Collections.emptyList();
        this.quarterlyIncomeTaxExpense = entity.getQuarterlyIncomeTaxExpense() != null ?
                Arrays.asList(entity.getQuarterlyIncomeTaxExpense()) : Collections.emptyList();
        this.quarterlyNetIncome = entity.getQuarterlyNetIncome() != null ?
                Arrays.asList(entity.getQuarterlyNetIncome()) : Collections.emptyList();
        this.quarterlyNetIncomeFromContinuingOperations = entity.getQuarterlyNetIncomeFromContinuingOperations() != null ?
                Arrays.asList(entity.getQuarterlyNetIncomeFromContinuingOperations()) : Collections.emptyList();
    }

}
