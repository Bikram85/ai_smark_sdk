package com.market.alphavantage.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockSummaryDTO {

    /* ================= REVENUE / INCOME STATEMENT ================= */
    @Data
    public static class RevenueDTO {
        // Annual
        public List<LocalDate> labels;
        public List<Long> totalRevenue;
        public List<Long> annualCostOfRevenue;
        public List<Long> grossProfit;
        public List<Long> operatingExpense;
        public List<Long> operatingIncome;
        public List<Long> incomeBeforeTax;
        public List<Long> netIncome;

        // Quarterly
        public List<LocalDate> quarterlyLabels;
        public List<Long> quarterlyTotalRevenue;
        public List<Long> quarterlyCostOfRevenue;
        public List<Long> quarterlyGrossProfit;
        public List<Long> quarterlyOperatingExpense;
        public List<Long> quarterlyOperatingIncome;
        public List<Long> quarterlyIncomeBeforeTax;
        public List<Long> quarterlyNetIncome;
    }

    /* ================= CASH FLOW ================= */
    @Data
    public static class CashFlowDTO {
        // Annual
        public List<LocalDate> labels;
        public List<Long> finance;
        public List<Long> investment;
        public List<Long> operating;
        public List<Long> changeInCash;

        // Quarterly
        public List<LocalDate> quarterlyLabels;
        public List<Long> quarterlyFinance;
        public List<Long> quarterlyInvestment;
        public List<Long> quarterlyOperating;
        public List<Long> quarterlyChangeInCash;
    }

    /* ================= BALANCE SHEET ================= */
    @Data
    public static class BalanceSheetDTO {
        // Annual
        public List<LocalDate> labels;
        public List<Long> totalAssets;
        public List<Long> liabilities;
        public List<Long> equity;

        // Quarterly
        public List<LocalDate> quarterlyLabels;
        public List<Long> quarterlyTotalAssets;
        public List<Long> quarterlyLiabilities;
        public List<Long> quarterlyEquity;
    }

    /* ================= STOCK PRICE ================= */
    @Data
    public static class PriceDTO {
        public List<LocalDate> labels;
        public List<Double> close;
        public List<Double> volume;
    }

    /* ================= TECHNICAL INDICATORS ================= */
    @Data
    public static class IndicatorsDTO {
        public List<LocalDate> labels;
        public List<Double> sma;
        public List<Double> rsi;
        public List<Double> macd;
    }

    /* ================= ANALYTICS ================= */
    @Data
    public static class AnalyticsDTO {
        public String symbol;
        public String exchange;
        public Double avgVolumeWeek;

        public Double support;
        public Double resistance;
        public Long totalCallOI;
        public Long totalPutOI;
        public Double pcr;
        public String bias;

        public String sector;
        public String industry;
        public Long marketCap;
        public Double peRatio;
        public String marketCapCategory;
        public String analystRating;
        public Double priceToBook;
        public Double percentInsiders;
        public Double percentInstitutions;
        public Double eps;

        public Integer revenueUpYears;
        public Integer grossProfitUpYears;
        public Integer netIncomeUpYears;

        public Integer revenueUpQuarters;
        public Integer grossProfitUpQuarters;
        public Integer netIncomeUpQuarters;

        public Double liabilityEquityPercent;

        public Integer positiveCashFlowYears;
        public Integer positiveCashFlowQuarters;

        public Double momentumScore;
        public Double fundamentalScore;
        public Double riskScore;
        public Double insiderScore;
        public Double optionsScore;

        public Double overallScore;
        public String recommendation;

        public Double smartMoneyScore;
        public Double gammaExposureScore;
        public Double volatilityScore;
        public Double liquidityScore;
        public Double factorScore;
        public Double hedgeFundAlpha;
        public Boolean goodEntry;

        public LocalDateTime updatedAt;
    }

    /* ================= ROOT ================= */
    public RevenueDTO revenue;
    public CashFlowDTO cashFlow;
    public BalanceSheetDTO balanceSheet;
    public PriceDTO price;
    public IndicatorsDTO indicators;
    public AnalyticsDTO analytics; // <-- Added Analytics here
}
