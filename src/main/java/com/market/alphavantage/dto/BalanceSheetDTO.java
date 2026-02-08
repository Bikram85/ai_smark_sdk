package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class BalanceSheetDTO {
    private String symbol;

    /* Annual */
    private List<LocalDate> annualFiscalDateEnding;
    private List<Long> annualTotalAssets;
    private List<Long> annualTotalLiabilities;
    private List<Long> annualTotalShareholderEquity;
    private List<Long> annualCashAndCashEquivalents;
    private List<Long> annualShortTermInvestments;
    private List<Long> annualNetReceivables;
    private List<Long> annualInventory;
    private List<Long> annualOtherCurrentAssets;
    private List<Long> annualOtherAssets;
    private List<Long> annualAccountsPayable;
    private List<Long> annualCurrentDebt;
    private List<Long> annualLongTermDebt;
    private List<Long> annualOtherCurrentLiabilities;
    private List<Long> annualOtherLiabilities;
    private List<Long> annualRetainedEarnings;
    private List<Long> annualTreasuryStock;

    /* Quarterly */
    private List<LocalDate> quarterlyFiscalDateEnding;
    private List<Long> quarterlyTotalAssets;
    private List<Long> quarterlyTotalLiabilities;
    private List<Long> quarterlyTotalShareholderEquity;
    private List<Long> quarterlyCashAndCashEquivalents;
    private List<Long> quarterlyShortTermInvestments;
    private List<Long> quarterlyNetReceivables;
    private List<Long> quarterlyInventory;
    private List<Long> quarterlyOtherCurrentAssets;
    private List<Long> quarterlyOtherAssets;
    private List<Long> quarterlyAccountsPayable;
    private List<Long> quarterlyCurrentDebt;
    private List<Long> quarterlyLongTermDebt;
    private List<Long> quarterlyOtherCurrentLiabilities;
    private List<Long> quarterlyOtherLiabilities;
    private List<Long> quarterlyRetainedEarnings;
    private List<Long> quarterlyTreasuryStock;
}
