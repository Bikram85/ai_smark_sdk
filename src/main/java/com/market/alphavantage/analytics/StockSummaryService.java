package com.market.alphavantage.analytics;

import com.market.alphavantage.dto.StockSummaryDTO;
import com.market.alphavantage.entity.*;
import com.market.alphavantage.repository.*;
import com.market.alphavantage.service.impl.processor.EquityTechincalIndicatorProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
@RequiredArgsConstructor
public class StockSummaryService {

    private final BalanceSheetRepository balanceSheetRepository;
    private final CashFlowRepository cashFlowRepository;
    private final IncomeStatementRepository incomeStatementRepository;
    private final StockPriceRepository stockPriceRepository;
    private final EquityTechnicalIndicatorRepository technicalRepository;
    private final OptionDashboardRepository optionDashboardRepository;
    private final AnalyticsRepository analyticsRepository;   // <-- inject Analytics

    @Autowired
    EquityTechincalIndicatorProcessor equityTechincalIndicatorProcessor;

    public StockSummaryDTO getStockSummary(String symbol) {
        StockSummaryDTO dto = new StockSummaryDTO();

        // ===== Revenue =====
        IncomeStatement is = incomeStatementRepository.findById(symbol).orElse(null);
        if (is != null) {
            StockSummaryDTO.RevenueDTO rev = new StockSummaryDTO.RevenueDTO();
            rev.labels = Arrays.asList(is.getAnnualFiscalDateEnding());
            rev.totalRevenue = Arrays.asList(is.getAnnualTotalRevenue());
            rev.annualCostOfRevenue = Arrays.asList(is.getAnnualCostOfRevenue());
            rev.grossProfit = Arrays.asList(is.getAnnualGrossProfit());
            rev.operatingExpense = Arrays.asList(is.getAnnualOperatingExpenses());
            rev.operatingIncome = Arrays.asList(is.getAnnualOperatingIncome());
            rev.incomeBeforeTax = Arrays.asList(is.getAnnualIncomeBeforeTax());
            rev.netIncome = Arrays.asList(is.getAnnualNetIncome());

            // Quarterly
            rev.quarterlyLabels = Arrays.asList(is.getQuarterlyFiscalDateEnding());
            rev.quarterlyTotalRevenue = Arrays.asList(is.getQuarterlyTotalRevenue());
            rev.quarterlyCostOfRevenue = Arrays.asList(is.getQuarterlyCostOfRevenue());
            rev.quarterlyGrossProfit = Arrays.asList(is.getQuarterlyGrossProfit());
            rev.quarterlyOperatingExpense = Arrays.asList(is.getQuarterlyOperatingExpenses());
            rev.quarterlyOperatingIncome = Arrays.asList(is.getQuarterlyOperatingIncome());
            rev.quarterlyIncomeBeforeTax = Arrays.asList(is.getQuarterlyIncomeBeforeTax());
            rev.quarterlyNetIncome = Arrays.asList(is.getQuarterlyNetIncome());

            dto.revenue = rev;
        }

        // ===== Cash Flow =====
        CashFlow cf = cashFlowRepository.findById(symbol).orElse(null);
        if (cf != null) {
            StockSummaryDTO.CashFlowDTO cash = new StockSummaryDTO.CashFlowDTO();
            cash.labels = Arrays.asList(cf.getAnnualFiscalDateEnding());
            cash.finance = Arrays.asList(cf.getAnnualCashflowFromFinancing());
            cash.investment = Arrays.asList(cf.getAnnualCashflowFromInvestment());
            cash.operating = Arrays.asList(cf.getAnnualOperatingCashflow());
            cash.changeInCash = Arrays.asList(cf.getAnnualChangeInCash());

            // Quarterly
            cash.quarterlyLabels = Arrays.asList(cf.getQuarterlyFiscalDateEnding());
            cash.quarterlyFinance = Arrays.asList(cf.getQuarterlyCashflowFromFinancing());
            cash.quarterlyInvestment = Arrays.asList(cf.getQuarterlyCashflowFromInvestment());
            cash.quarterlyOperating = Arrays.asList(cf.getQuarterlyOperatingCashflow());
            cash.quarterlyChangeInCash = Arrays.asList(cf.getQuarterlyChangeInCash());

            dto.cashFlow = cash;
        }

        // ===== Balance Sheet =====
        BalanceSheet bs = balanceSheetRepository.findById(symbol).orElse(null);
        if (bs != null) {
            StockSummaryDTO.BalanceSheetDTO balance = new StockSummaryDTO.BalanceSheetDTO();
            balance.labels = Arrays.asList(bs.getAnnualFiscalDateEnding());
            balance.totalAssets = Arrays.asList(bs.getAnnualTotalAssets());
            balance.liabilities = Arrays.asList(bs.getAnnualTotalLiabilities());
            balance.equity = Arrays.asList(bs.getAnnualTotalShareholderEquity());

            // Quarterly
            balance.quarterlyLabels = Arrays.asList(bs.getQuarterlyFiscalDateEnding());
            balance.quarterlyTotalAssets = Arrays.asList(bs.getQuarterlyTotalAssets());
            balance.quarterlyLiabilities = Arrays.asList(bs.getQuarterlyTotalLiabilities());
            balance.quarterlyEquity = Arrays.asList(bs.getQuarterlyTotalShareholderEquity());

            dto.balanceSheet = balance;
        }

        // ===== Stock Price =====
        StockPrice sp = stockPriceRepository.findById(symbol).orElse(null);
        if (sp != null) {
            StockSummaryDTO.PriceDTO price = new StockSummaryDTO.PriceDTO();
            price.labels = Arrays.asList(sp.getTradeDates());
            price.close = Arrays.asList(sp.getClose());
            price.volume = Arrays.asList(sp.getVolume());
            dto.price = price;
        }

        // ===== Technical Indicators =====
        equityTechincalIndicatorProcessor.processSymbol(symbol);
        List<EquityTechnicalIndicator> indicators = technicalRepository.getData(symbol);

        if (indicators != null && !indicators.isEmpty()) {
            StockSummaryDTO.IndicatorsDTO ind = new StockSummaryDTO.IndicatorsDTO();

            for (EquityTechnicalIndicator i : indicators) {
                String function = i.getFunction().toUpperCase();

                if (function.equals("SMA")) {
                    String period = i.getTimePeriod() != null ? i.getTimePeriod().toString() : "NA";
                    String key = "SMA_" + period;

                    ind.smaMap.put(key, Arrays.asList(i.getValues()));
                    ind.smaLabelsMap.put(key, Arrays.asList(i.getDates())); // separate labels per SMA
                }
                else if (function.equals("RSI")) {
                    ind.rsi = Arrays.asList(i.getValues());
                    ind.rsiLabels = Arrays.asList(i.getDates());

                } else if (function.equals("MACD")) {
                    ind.macd = Arrays.asList(i.getValues());
                    ind.macdLabels = Arrays.asList(i.getDates());
                }
            }

            dto.indicators = ind;
        }


        // ===== Analytics =====
        Analytics analytics = analyticsRepository.findById(symbol).orElse(null);
        if (analytics != null) {
            StockSummaryDTO.AnalyticsDTO a = new StockSummaryDTO.AnalyticsDTO();
            a.symbol = analytics.getSymbol();
            a.exchange = analytics.getExchange();
            a.avgVolumeWeek = analytics.getAvgVolumeWeek();
            a.support = analytics.getSupport();
            a.resistance = analytics.getResistance();
            a.callPutVolumeRatio = analytics.getCallPutVolumeRatio();
            a.callPutOIRatio = analytics.getCallPutOIRatio();
            a.pcr = analytics.getPcr();
            a.bias = analytics.getBias();
            a.sector = analytics.getSector();
            a.industry = analytics.getIndustry();
            a.marketCap = analytics.getMarketCap();
            a.peRatio = analytics.getPeRatio();
            a.marketCapCategory = analytics.getMarketCapCategory();
            a.analystRating = analytics.getAnalystRating();
            a.priceToBook = analytics.getPriceToBook();
            a.percentInsiders = analytics.getPercentInsiders();
            a.percentInstitutions = analytics.getPercentInstitutions();
            a.eps = analytics.getEps();
            a.revenueUpYears = analytics.getRevenueUpYears();
            a.grossProfitUpYears = analytics.getGrossProfitUpYears();
            a.netIncomeUpYears = analytics.getNetIncomeUpYears();
            a.revenueUpQuarters = analytics.getRevenueUpQuarters();
            a.grossProfitUpQuarters = analytics.getGrossProfitUpQuarters();
            a.netIncomeUpQuarters = analytics.getNetIncomeUpQuarters();
            a.liabilityEquityPercent = analytics.getLiabilityEquityPercent();
            a.positiveCashFlowYears = analytics.getPositiveCashFlowYears();
            a.positiveCashFlowQuarters = analytics.getPositiveCashFlowQuarters();
            a.momentumScore = analytics.getMomentumScore();
            a.fundamentalScore = analytics.getFundamentalScore();
            a.riskScore = analytics.getRiskScore();
            a.insiderScore = analytics.getInsiderScore();
            a.optionsScore = analytics.getOptionsScore();
            a.overallScore = analytics.getOverallScore();
            a.recommendation = analytics.getRecommendation();
            a.smartMoneyScore = analytics.getSmartMoneyScore();
            a.gammaExposureScore = analytics.getGammaExposureScore();
            a.volatilityScore = analytics.getVolatilityScore();
            a.liquidityScore = analytics.getLiquidityScore();
            a.factorScore = analytics.getFactorScore();
            a.hedgeFundAlpha = analytics.getHedgeFundAlpha();
            a.goodEntry = analytics.getGoodEntry();
            a.updatedAt = analytics.getUpdatedAt();

            dto.analytics = a;
        }

        return dto;
    }
}
