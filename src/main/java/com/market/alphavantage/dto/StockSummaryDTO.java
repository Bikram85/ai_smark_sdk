package com.market.alphavantage.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class StockSummaryDTO {

    public static class RevenueDTO {
        public List<LocalDate> labels;
        public List<Long> totalRevenue;
        public List<Long> annualCostOfRevenue;
        public List<Long> grossProfit;
        public List<Long> operatingExpense;
        public List<Long> operatingIncome;
        public List<Long> incomeBeforeTax;
        public List<Long> netIncome;
    }

    public static class CashFlowDTO {
        public List<LocalDate> labels;
        public List<Long> finance;
        public List<Long> investment;
        public List<Long> operating;
        public List<Long> changeInCash;
    }

    public static class BalanceSheetDTO {
        public List<LocalDate> labels;
        public List<Long> totalAssets;
        public List<Long> liabilities;
        public List<Long> equity;
    }

    public static class PriceDTO {
        public List<LocalDate> labels;
        public List<Double> close;
        public List<Double> volume;
    }

    public static class IndicatorsDTO {
        public List<LocalDate> labels;
        public List<Double> sma;
        public List<Double> rsi;
        public List<Double> macd;
    }

    public RevenueDTO revenue;
    public CashFlowDTO cashFlow;
    public BalanceSheetDTO balanceSheet;
    public PriceDTO price;
    public IndicatorsDTO indicators;
}
