package com.market.alphavantage.dto;

import com.market.alphavantage.entity.CashFlow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashFlowDTO {

    private String symbol;

    /* ---------- Annual ---------- */
    private List<LocalDate> annualFiscalDateEnding;
    private List<Long> annualOperatingCashflow;
    private List<Long> annualPaymentsForOperatingActivities;
    private List<Long> annualProceedsFromOperatingActivities;
    private List<Long> annualChangeInCash;
    private List<Long> annualCashflowFromInvestment;
    private List<Long> annualCashflowFromFinancing;
    private List<Long> annualProceedsFromRepaymentsOfShortTermDebt;
    private List<Long> annualPaymentsForRepurchaseOfCommonStock;
    private List<Long> annualPaymentsForRepurchaseOfEquity;
    private List<Long> annualPaymentsForRepurchaseOfPreferredStock;
    private List<Long> annualDividendsPaid;
    private List<Long> annualDividendsPaidOnCommonStock;
    private List<Long> annualDividendsPaidOnPreferredStock;
    private List<Long> annualProceedsFromIssuanceOfCommonStock;
    private List<Long> annualProceedsFromIssuanceOfLongTermDebt;
    private List<Long> annualProceedsFromIssuanceOfPreferredStock;
    private List<Long> annualProceedsFromRepurchaseOfEquity;
    private List<Long> annualOtherCashflowFromFinancingActivities;
    private List<Long> annualNetBorrowings;

    /* ---------- Quarterly ---------- */
    private List<LocalDate> quarterlyFiscalDateEnding;
    private List<Long> quarterlyOperatingCashflow;
    private List<Long> quarterlyPaymentsForOperatingActivities;
    private List<Long> quarterlyProceedsFromOperatingActivities;
    private List<Long> quarterlyChangeInCash;
    private List<Long> quarterlyCashflowFromInvestment;
    private List<Long> quarterlyCashflowFromFinancing;
    private List<Long> quarterlyProceedsFromRepaymentsOfShortTermDebt;
    private List<Long> quarterlyPaymentsForRepurchaseOfCommonStock;
    private List<Long> quarterlyPaymentsForRepurchaseOfEquity;
    private List<Long> quarterlyPaymentsForRepurchaseOfPreferredStock;
    private List<Long> quarterlyDividendsPaid;
    private List<Long> quarterlyDividendsPaidOnCommonStock;
    private List<Long> quarterlyDividendsPaidOnPreferredStock;
    private List<Long> quarterlyProceedsFromIssuanceOfCommonStock;
    private List<Long> quarterlyProceedsFromIssuanceOfLongTermDebt;
    private List<Long> quarterlyProceedsFromIssuanceOfPreferredStock;
    private List<Long> quarterlyProceedsFromRepurchaseOfEquity;
    private List<Long> quarterlyOtherCashflowFromFinancingActivities;
    private List<Long> quarterlyNetBorrowings;

    /* ===== Constructor from CashFlow entity ===== */
    public CashFlowDTO(CashFlow entity) {
        this.symbol = entity.getSymbol();

        // Annual
        this.annualFiscalDateEnding = entity.getAnnualFiscalDateEnding() != null ?
                Arrays.asList(entity.getAnnualFiscalDateEnding()) : Collections.emptyList();
        this.annualOperatingCashflow = entity.getAnnualOperatingCashflow() != null ?
                Arrays.asList(entity.getAnnualOperatingCashflow()) : Collections.emptyList();
        this.annualPaymentsForOperatingActivities = entity.getAnnualPaymentsForOperatingActivities() != null ?
                Arrays.asList(entity.getAnnualPaymentsForOperatingActivities()) : Collections.emptyList();
        this.annualProceedsFromOperatingActivities = entity.getAnnualProceedsFromOperatingActivities() != null ?
                Arrays.asList(entity.getAnnualProceedsFromOperatingActivities()) : Collections.emptyList();
        this.annualChangeInCash = entity.getAnnualChangeInCash() != null ?
                Arrays.asList(entity.getAnnualChangeInCash()) : Collections.emptyList();
        this.annualCashflowFromInvestment = entity.getAnnualCashflowFromInvestment() != null ?
                Arrays.asList(entity.getAnnualCashflowFromInvestment()) : Collections.emptyList();
        this.annualCashflowFromFinancing = entity.getAnnualCashflowFromFinancing() != null ?
                Arrays.asList(entity.getAnnualCashflowFromFinancing()) : Collections.emptyList();
        this.annualProceedsFromRepaymentsOfShortTermDebt = entity.getAnnualProceedsFromRepaymentsOfShortTermDebt() != null ?
                Arrays.asList(entity.getAnnualProceedsFromRepaymentsOfShortTermDebt()) : Collections.emptyList();
        this.annualPaymentsForRepurchaseOfCommonStock = entity.getAnnualPaymentsForRepurchaseOfCommonStock() != null ?
                Arrays.asList(entity.getAnnualPaymentsForRepurchaseOfCommonStock()) : Collections.emptyList();
        this.annualPaymentsForRepurchaseOfEquity = entity.getAnnualPaymentsForRepurchaseOfEquity() != null ?
                Arrays.asList(entity.getAnnualPaymentsForRepurchaseOfEquity()) : Collections.emptyList();
        this.annualPaymentsForRepurchaseOfPreferredStock = entity.getAnnualPaymentsForRepurchaseOfPreferredStock() != null ?
                Arrays.asList(entity.getAnnualPaymentsForRepurchaseOfPreferredStock()) : Collections.emptyList();
        this.annualDividendsPaid = entity.getAnnualDividendsPaid() != null ?
                Arrays.asList(entity.getAnnualDividendsPaid()) : Collections.emptyList();
        this.annualDividendsPaidOnCommonStock = entity.getAnnualDividendsPaidOnCommonStock() != null ?
                Arrays.asList(entity.getAnnualDividendsPaidOnCommonStock()) : Collections.emptyList();
        this.annualDividendsPaidOnPreferredStock = entity.getAnnualDividendsPaidOnPreferredStock() != null ?
                Arrays.asList(entity.getAnnualDividendsPaidOnPreferredStock()) : Collections.emptyList();
        this.annualProceedsFromIssuanceOfCommonStock = entity.getAnnualProceedsFromIssuanceOfCommonStock() != null ?
                Arrays.asList(entity.getAnnualProceedsFromIssuanceOfCommonStock()) : Collections.emptyList();
        this.annualProceedsFromIssuanceOfLongTermDebt = entity.getAnnualProceedsFromIssuanceOfLongTermDebt() != null ?
                Arrays.asList(entity.getAnnualProceedsFromIssuanceOfLongTermDebt()) : Collections.emptyList();
        this.annualProceedsFromIssuanceOfPreferredStock = entity.getAnnualProceedsFromIssuanceOfPreferredStock() != null ?
                Arrays.asList(entity.getAnnualProceedsFromIssuanceOfPreferredStock()) : Collections.emptyList();
        this.annualProceedsFromRepurchaseOfEquity = entity.getAnnualProceedsFromRepurchaseOfEquity() != null ?
                Arrays.asList(entity.getAnnualProceedsFromRepurchaseOfEquity()) : Collections.emptyList();
        this.annualOtherCashflowFromFinancingActivities = entity.getAnnualOtherCashflowFromFinancingActivities() != null ?
                Arrays.asList(entity.getAnnualOtherCashflowFromFinancingActivities()) : Collections.emptyList();
        this.annualNetBorrowings = entity.getAnnualNetBorrowings() != null ?
                Arrays.asList(entity.getAnnualNetBorrowings()) : Collections.emptyList();

        // Quarterly
        this.quarterlyFiscalDateEnding = entity.getQuarterlyFiscalDateEnding() != null ?
                Arrays.asList(entity.getQuarterlyFiscalDateEnding()) : Collections.emptyList();
        this.quarterlyOperatingCashflow = entity.getQuarterlyOperatingCashflow() != null ?
                Arrays.asList(entity.getQuarterlyOperatingCashflow()) : Collections.emptyList();
        this.quarterlyPaymentsForOperatingActivities = entity.getQuarterlyPaymentsForOperatingActivities() != null ?
                Arrays.asList(entity.getQuarterlyPaymentsForOperatingActivities()) : Collections.emptyList();
        this.quarterlyProceedsFromOperatingActivities = entity.getQuarterlyProceedsFromOperatingActivities() != null ?
                Arrays.asList(entity.getQuarterlyProceedsFromOperatingActivities()) : Collections.emptyList();
        this.quarterlyChangeInCash = entity.getQuarterlyChangeInCash() != null ?
                Arrays.asList(entity.getQuarterlyChangeInCash()) : Collections.emptyList();
        this.quarterlyCashflowFromInvestment = entity.getQuarterlyCashflowFromInvestment() != null ?
                Arrays.asList(entity.getQuarterlyCashflowFromInvestment()) : Collections.emptyList();
        this.quarterlyCashflowFromFinancing = entity.getQuarterlyCashflowFromFinancing() != null ?
                Arrays.asList(entity.getQuarterlyCashflowFromFinancing()) : Collections.emptyList();
        this.quarterlyProceedsFromRepaymentsOfShortTermDebt = entity.getQuarterlyProceedsFromRepaymentsOfShortTermDebt() != null ?
                Arrays.asList(entity.getQuarterlyProceedsFromRepaymentsOfShortTermDebt()) : Collections.emptyList();
        this.quarterlyPaymentsForRepurchaseOfCommonStock = entity.getQuarterlyPaymentsForRepurchaseOfCommonStock() != null ?
                Arrays.asList(entity.getQuarterlyPaymentsForRepurchaseOfCommonStock()) : Collections.emptyList();
        this.quarterlyPaymentsForRepurchaseOfEquity = entity.getQuarterlyPaymentsForRepurchaseOfEquity() != null ?
                Arrays.asList(entity.getQuarterlyPaymentsForRepurchaseOfEquity()) : Collections.emptyList();
        this.quarterlyPaymentsForRepurchaseOfPreferredStock = entity.getQuarterlyPaymentsForRepurchaseOfPreferredStock() != null ?
                Arrays.asList(entity.getQuarterlyPaymentsForRepurchaseOfPreferredStock()) : Collections.emptyList();
        this.quarterlyDividendsPaid = entity.getQuarterlyDividendsPaid() != null ?
                Arrays.asList(entity.getQuarterlyDividendsPaid()) : Collections.emptyList();
        this.quarterlyDividendsPaidOnCommonStock = entity.getQuarterlyDividendsPaidOnCommonStock() != null ?
                Arrays.asList(entity.getQuarterlyDividendsPaidOnCommonStock()) : Collections.emptyList();
        this.quarterlyDividendsPaidOnPreferredStock = entity.getQuarterlyDividendsPaidOnPreferredStock() != null ?
                Arrays.asList(entity.getQuarterlyDividendsPaidOnPreferredStock()) : Collections.emptyList();
        this.quarterlyProceedsFromIssuanceOfCommonStock = entity.getQuarterlyProceedsFromIssuanceOfCommonStock() != null ?
                Arrays.asList(entity.getQuarterlyProceedsFromIssuanceOfCommonStock()) : Collections.emptyList();
        this.quarterlyProceedsFromIssuanceOfLongTermDebt = entity.getQuarterlyProceedsFromIssuanceOfLongTermDebt() != null ?
                Arrays.asList(entity.getQuarterlyProceedsFromIssuanceOfLongTermDebt()) : Collections.emptyList();
        this.quarterlyProceedsFromIssuanceOfPreferredStock = entity.getQuarterlyProceedsFromIssuanceOfPreferredStock() != null ?
                Arrays.asList(entity.getQuarterlyProceedsFromIssuanceOfPreferredStock()) : Collections.emptyList();
        this.quarterlyProceedsFromRepurchaseOfEquity = entity.getQuarterlyProceedsFromRepurchaseOfEquity() != null ?
                Arrays.asList(entity.getQuarterlyProceedsFromRepurchaseOfEquity()) : Collections.emptyList();
        this.quarterlyOtherCashflowFromFinancingActivities = entity.getQuarterlyOtherCashflowFromFinancingActivities() != null ?
                Arrays.asList(entity.getQuarterlyOtherCashflowFromFinancingActivities()) : Collections.emptyList();
        this.quarterlyNetBorrowings = entity.getQuarterlyNetBorrowings() != null ?
                Arrays.asList(entity.getQuarterlyNetBorrowings()) : Collections.emptyList();
    }
}
