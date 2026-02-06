package com.market.alphavantage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cash_flow")
@Data
public class CashFlow {

    @Id
    private String symbol;

    /* ---------- Annual ---------- */
    @ElementCollection private List<LocalDate> annualFiscalDateEnding;
    @ElementCollection private List<Long> annualOperatingCashflow;
    @ElementCollection private List<Long> annualPaymentsForOperatingActivities;
    @ElementCollection private List<Long> annualProceedsFromOperatingActivities;
    @ElementCollection private List<Long> annualChangeInCash;
    @ElementCollection private List<Long> annualCashflowFromInvestment;
    @ElementCollection private List<Long> annualCashflowFromFinancing;
    @ElementCollection private List<Long> annualProceedsFromRepaymentsOfShortTermDebt;
    @ElementCollection private List<Long> annualPaymentsForRepurchaseOfCommonStock;
    @ElementCollection private List<Long> annualPaymentsForRepurchaseOfEquity;
    @ElementCollection private List<Long> annualPaymentsForRepurchaseOfPreferredStock;
    @ElementCollection private List<Long> annualDividendsPaid;
    @ElementCollection private List<Long> annualDividendsPaidOnCommonStock;
    @ElementCollection private List<Long> annualDividendsPaidOnPreferredStock;
    @ElementCollection private List<Long> annualProceedsFromIssuanceOfCommonStock;
    @ElementCollection private List<Long> annualProceedsFromIssuanceOfLongTermDebt;
    @ElementCollection private List<Long> annualProceedsFromIssuanceOfPreferredStock;
    @ElementCollection private List<Long> annualProceedsFromRepurchaseOfEquity;
    @ElementCollection private List<Long> annualOtherCashflowFromFinancingActivities;
    @ElementCollection private List<Long> annualNetBorrowings;

    /* ---------- Quarterly ---------- */
    @ElementCollection private List<LocalDate> quarterlyFiscalDateEnding;
    @ElementCollection private List<Long> quarterlyOperatingCashflow;
    @ElementCollection private List<Long> quarterlyPaymentsForOperatingActivities;
    @ElementCollection private List<Long> quarterlyProceedsFromOperatingActivities;
    @ElementCollection private List<Long> quarterlyChangeInCash;
    @ElementCollection private List<Long> quarterlyCashflowFromInvestment;
    @ElementCollection private List<Long> quarterlyCashflowFromFinancing;
    @ElementCollection private List<Long> quarterlyProceedsFromRepaymentsOfShortTermDebt;
    @ElementCollection private List<Long> quarterlyPaymentsForRepurchaseOfCommonStock;
    @ElementCollection private List<Long> quarterlyPaymentsForRepurchaseOfEquity;
    @ElementCollection private List<Long> quarterlyPaymentsForRepurchaseOfPreferredStock;
    @ElementCollection private List<Long> quarterlyDividendsPaid;
    @ElementCollection private List<Long> quarterlyDividendsPaidOnCommonStock;
    @ElementCollection private List<Long> quarterlyDividendsPaidOnPreferredStock;
    @ElementCollection private List<Long> quarterlyProceedsFromIssuanceOfCommonStock;
    @ElementCollection private List<Long> quarterlyProceedsFromIssuanceOfLongTermDebt;
    @ElementCollection private List<Long> quarterlyProceedsFromIssuanceOfPreferredStock;
    @ElementCollection private List<Long> quarterlyProceedsFromRepurchaseOfEquity;
    @ElementCollection private List<Long> quarterlyOtherCashflowFromFinancingActivities;
    @ElementCollection private List<Long> quarterlyNetBorrowings;
}

