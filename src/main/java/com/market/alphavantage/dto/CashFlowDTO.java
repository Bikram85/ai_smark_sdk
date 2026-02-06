package com.market.alphavantage.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
}
