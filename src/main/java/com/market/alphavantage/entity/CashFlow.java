package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "cash_flow")
public class CashFlow {

    @Id
    @Column(name = "symbol")
    private String symbol;

    /* ================= ANNUAL ================= */

    @Column(name = "annual_fiscal_date_ending")
    private LocalDate[] annualFiscalDateEnding;

    @Column(name = "annual_operating_cashflow")
    private Long[] annualOperatingCashflow;

    @Column(name = "annual_payments_for_operating_activities")
    private Long[] annualPaymentsForOperatingActivities;

    @Column(name = "annual_proceeds_from_operating_activities")
    private Long[] annualProceedsFromOperatingActivities;

    @Column(name = "annual_change_in_cash")
    private Long[] annualChangeInCash;

    @Column(name = "annual_cashflow_from_investment")
    private Long[] annualCashflowFromInvestment;

    @Column(name = "annual_cashflow_from_financing")
    private Long[] annualCashflowFromFinancing;

    @Column(name = "annual_proceeds_from_repayments_of_short_term_debt")
    private Long[] annualProceedsFromRepaymentsOfShortTermDebt;

    @Column(name = "annual_payments_for_repurchase_of_common_stock")
    private Long[] annualPaymentsForRepurchaseOfCommonStock;

    @Column(name = "annual_payments_for_repurchase_of_equity")
    private Long[] annualPaymentsForRepurchaseOfEquity;

    @Column(name = "annual_payments_for_repurchase_of_preferred_stock")
    private Long[] annualPaymentsForRepurchaseOfPreferredStock;

    @Column(name = "annual_dividends_paid")
    private Long[] annualDividendsPaid;

    @Column(name = "annual_dividends_paid_on_common_stock")
    private Long[] annualDividendsPaidOnCommonStock;

    @Column(name = "annual_dividends_paid_on_preferred_stock")
    private Long[] annualDividendsPaidOnPreferredStock;

    @Column(name = "annual_proceeds_from_issuance_of_common_stock")
    private Long[] annualProceedsFromIssuanceOfCommonStock;

    @Column(name = "annual_proceeds_from_issuance_of_long_term_debt")
    private Long[] annualProceedsFromIssuanceOfLongTermDebt;

    @Column(name = "annual_proceeds_from_issuance_of_preferred_stock")
    private Long[] annualProceedsFromIssuanceOfPreferredStock;

    @Column(name = "annual_proceeds_from_repurchase_of_equity")
    private Long[] annualProceedsFromRepurchaseOfEquity;

    @Column(name = "annual_other_cashflow_from_financing_activities")
    private Long[] annualOtherCashflowFromFinancingActivities;

    @Column(name = "annual_net_borrowings")
    private Long[] annualNetBorrowings;

    /* ================= QUARTERLY ================= */

    @Column(name = "quarterly_fiscal_date_ending")
    private LocalDate[] quarterlyFiscalDateEnding;

    @Column(name = "quarterly_operating_cashflow")
    private Long[] quarterlyOperatingCashflow;

    @Column(name = "quarterly_payments_for_operating_activities")
    private Long[] quarterlyPaymentsForOperatingActivities;

    @Column(name = "quarterly_proceeds_from_operating_activities")
    private Long[] quarterlyProceedsFromOperatingActivities;

    @Column(name = "quarterly_change_in_cash")
    private Long[] quarterlyChangeInCash;

    @Column(name = "quarterly_cashflow_from_investment")
    private Long[] quarterlyCashflowFromInvestment;

    @Column(name = "quarterly_cashflow_from_financing")
    private Long[] quarterlyCashflowFromFinancing;

    @Column(name = "quarterly_proceeds_from_repayments_of_short_term_debt")
    private Long[] quarterlyProceedsFromRepaymentsOfShortTermDebt;

    @Column(name = "quarterly_payments_for_repurchase_of_common_stock")
    private Long[] quarterlyPaymentsForRepurchaseOfCommonStock;

    @Column(name = "quarterly_payments_for_repurchase_of_equity")
    private Long[] quarterlyPaymentsForRepurchaseOfEquity;

    @Column(name = "quarterly_payments_for_repurchase_of_preferred_stock")
    private Long[] quarterlyPaymentsForRepurchaseOfPreferredStock;

    @Column(name = "quarterly_dividends_paid")
    private Long[] quarterlyDividendsPaid;

    @Column(name = "quarterly_dividends_paid_on_common_stock")
    private Long[] quarterlyDividendsPaidOnCommonStock;

    @Column(name = "quarterly_dividends_paid_on_preferred_stock")
    private Long[] quarterlyDividendsPaidOnPreferredStock;

    @Column(name = "quarterly_proceeds_from_issuance_of_common_stock")
    private Long[] quarterlyProceedsFromIssuanceOfCommonStock;

    @Column(name = "quarterly_proceeds_from_issuance_of_long_term_debt")
    private Long[] quarterlyProceedsFromIssuanceOfLongTermDebt;

    @Column(name = "quarterly_proceeds_from_issuance_of_preferred_stock")
    private Long[] quarterlyProceedsFromIssuanceOfPreferredStock;

    @Column(name = "quarterly_proceeds_from_repurchase_of_equity")
    private Long[] quarterlyProceedsFromRepurchaseOfEquity;

    @Column(name = "quarterly_other_cashflow_from_financing_activities")
    private Long[] quarterlyOtherCashflowFromFinancingActivities;

    @Column(name = "quarterly_net_borrowings")
    private Long[] quarterlyNetBorrowings;
}
