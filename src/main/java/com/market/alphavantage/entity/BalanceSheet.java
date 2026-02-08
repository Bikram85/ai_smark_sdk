package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "balance_sheet")
public class BalanceSheet {

    @Id
    private String symbol;

    /* ================= ANNUAL ================= */

    @Column(name = "annual_fiscal_date_ending")
    private LocalDate[] annualFiscalDateEnding;

    @Column(name = "annual_total_assets", columnDefinition = "BIGINT[]")
    private Long[] annualTotalAssets;

    @Column(name = "annual_total_liabilities", columnDefinition = "BIGINT[]")
    private Long[] annualTotalLiabilities;

    @Column(name = "annual_total_shareholder_equity", columnDefinition = "BIGINT[]")
    private Long[] annualTotalShareholderEquity;

    @Column(name = "annual_cash_and_cash_equivalents", columnDefinition = "BIGINT[]")
    private Long[] annualCashAndCashEquivalents;

    @Column(name = "annual_short_term_investments", columnDefinition = "BIGINT[]")
    private Long[] annualShortTermInvestments;

    @Column(name = "annual_net_receivables", columnDefinition = "BIGINT[]")
    private Long[] annualNetReceivables;

    @Column(name = "annual_inventory", columnDefinition = "BIGINT[]")
    private Long[] annualInventory;

    @Column(name = "annual_other_current_assets", columnDefinition = "BIGINT[]")
    private Long[] annualOtherCurrentAssets;

    @Column(name = "annual_other_assets", columnDefinition = "BIGINT[]")
    private Long[] annualOtherAssets;

    @Column(name = "annual_accounts_payable", columnDefinition = "BIGINT[]")
    private Long[] annualAccountsPayable;

    @Column(name = "annual_current_debt", columnDefinition = "BIGINT[]")
    private Long[] annualCurrentDebt;

    @Column(name = "annual_long_term_debt", columnDefinition = "BIGINT[]")
    private Long[] annualLongTermDebt;

    @Column(name = "annual_other_current_liabilities", columnDefinition = "BIGINT[]")
    private Long[] annualOtherCurrentLiabilities;

    @Column(name = "annual_other_liabilities", columnDefinition = "BIGINT[]")
    private Long[] annualOtherLiabilities;

    @Column(name = "annual_retained_earnings", columnDefinition = "BIGINT[]")
    private Long[] annualRetainedEarnings;

    @Column(name = "annual_treasury_stock", columnDefinition = "BIGINT[]")
    private Long[] annualTreasuryStock;

    /* ================= QUARTERLY ================= */

    @Column(name = "quarterly_fiscal_date_ending")
    private LocalDate[] quarterlyFiscalDateEnding;

    @Column(name = "quarterly_total_assets", columnDefinition = "BIGINT[]")
    private Long[] quarterlyTotalAssets;

    @Column(name = "quarterly_total_liabilities", columnDefinition = "BIGINT[]")
    private Long[] quarterlyTotalLiabilities;

    @Column(name = "quarterly_total_shareholder_equity", columnDefinition = "BIGINT[]")
    private Long[] quarterlyTotalShareholderEquity;

    @Column(name = "quarterly_cash_and_cash_equivalents", columnDefinition = "BIGINT[]")
    private Long[] quarterlyCashAndCashEquivalents;

    @Column(name = "quarterly_short_term_investments", columnDefinition = "BIGINT[]")
    private Long[] quarterlyShortTermInvestments;

    @Column(name = "quarterly_net_receivables", columnDefinition = "BIGINT[]")
    private Long[] quarterlyNetReceivables;

    @Column(name = "quarterly_inventory", columnDefinition = "BIGINT[]")
    private Long[] quarterlyInventory;

    @Column(name = "quarterly_other_current_assets", columnDefinition = "BIGINT[]")
    private Long[] quarterlyOtherCurrentAssets;

    @Column(name = "quarterly_other_assets", columnDefinition = "BIGINT[]")
    private Long[] quarterlyOtherAssets;

    @Column(name = "quarterly_accounts_payable", columnDefinition = "BIGINT[]")
    private Long[] quarterlyAccountsPayable;

    @Column(name = "quarterly_current_debt", columnDefinition = "BIGINT[]")
    private Long[] quarterlyCurrentDebt;

    @Column(name = "quarterly_long_term_debt", columnDefinition = "BIGINT[]")
    private Long[] quarterlyLongTermDebt;

    @Column(name = "quarterly_other_current_liabilities", columnDefinition = "BIGINT[]")
    private Long[] quarterlyOtherCurrentLiabilities;

    @Column(name = "quarterly_other_liabilities", columnDefinition = "BIGINT[]")
    private Long[] quarterlyOtherLiabilities;

    @Column(name = "quarterly_retained_earnings", columnDefinition = "BIGINT[]")
    private Long[] quarterlyRetainedEarnings;

    @Column(name = "quarterly_treasury_stock", columnDefinition = "BIGINT[]")
    private Long[] quarterlyTreasuryStock;
}
