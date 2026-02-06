package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "balance_sheet")
@Data
public class BalanceSheet {

    @Id
    private String symbol;

    /* ---------- Annual ---------- */
    @ElementCollection
    private List<LocalDate> annualFiscalDateEnding;
    @ElementCollection private List<Long> annualTotalAssets;
    @ElementCollection private List<Long> annualTotalLiabilities;
    @ElementCollection private List<Long> annualTotalShareholderEquity;
    @ElementCollection private List<Long> annualCashAndCashEquivalents;
    @ElementCollection private List<Long> annualShortTermInvestments;
    @ElementCollection private List<Long> annualNetReceivables;
    @ElementCollection private List<Long> annualInventory;
    @ElementCollection private List<Long> annualOtherCurrentAssets;
    @ElementCollection private List<Long> annualOtherAssets;
    @ElementCollection private List<Long> annualAccountsPayable;
    @ElementCollection private List<Long> annualCurrentDebt;
    @ElementCollection private List<Long> annualLongTermDebt;
    @ElementCollection private List<Long> annualOtherCurrentLiabilities;
    @ElementCollection private List<Long> annualOtherLiabilities;
    @ElementCollection private List<Long> annualRetainedEarnings;
    @ElementCollection private List<Long> annualTreasuryStock;

    /* ---------- Quarterly ---------- */
    @ElementCollection private List<LocalDate> quarterlyFiscalDateEnding;
    @ElementCollection private List<Long> quarterlyTotalAssets;
    @ElementCollection private List<Long> quarterlyTotalLiabilities;
    @ElementCollection private List<Long> quarterlyTotalShareholderEquity;
    @ElementCollection private List<Long> quarterlyCashAndCashEquivalents;
    @ElementCollection private List<Long> quarterlyShortTermInvestments;
    @ElementCollection private List<Long> quarterlyNetReceivables;
    @ElementCollection private List<Long> quarterlyInventory;
    @ElementCollection private List<Long> quarterlyOtherCurrentAssets;
    @ElementCollection private List<Long> quarterlyOtherAssets;
    @ElementCollection private List<Long> quarterlyAccountsPayable;
    @ElementCollection private List<Long> quarterlyCurrentDebt;
    @ElementCollection private List<Long> quarterlyLongTermDebt;
    @ElementCollection private List<Long> quarterlyOtherCurrentLiabilities;
    @ElementCollection private List<Long> quarterlyOtherLiabilities;
    @ElementCollection private List<Long> quarterlyRetainedEarnings;
    @ElementCollection private List<Long> quarterlyTreasuryStock;
}
