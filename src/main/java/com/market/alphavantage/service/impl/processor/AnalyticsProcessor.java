package com.market.alphavantage.service.impl.processor;

import com.market.alphavantage.entity.*;
import com.market.alphavantage.repository.*;
import com.market.alphavantage.util.AnalyticsMath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AnalyticsProcessor {

    private final StockPriceRepository priceRepo;
    private final OptionDashboardRepository optionRepo;
    private final CompanyOverviewRepository companyRepo;
    private final IncomeStatementRepository incomeRepo;
    private final BalanceSheetRepository balanceRepo;
    private final CashFlowRepository cashRepo;
    private final InsiderTransactionRepository insiderRepo;
    private final AnalyticsRepository analyticsRepo;

    /* ================= CORE ================= */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSymbol(Symbol sym) {
        String symbol = sym.getSymbol();
        Analytics a = new Analytics();
        a.setSymbol(symbol);
        a.setExchange(sym.getExchange());

        StockPrice price = priceRepo.findById(symbol).orElse(null);
        OptionDashboard opt = optionRepo.findTopBySymbolOrderByIdDesc(symbol).orElse(null);
        CompanyOverview co = companyRepo.findById(symbol).orElse(null);
        IncomeStatement inc = incomeRepo.findById(symbol).orElse(null);
        BalanceSheet bal = balanceRepo.findById(symbol).orElse(null);
        CashFlow cf = cashRepo.findById(symbol).orElse(null);
        InsiderTransaction insider = insiderRepo.findById(symbol).orElse(null);

        /* ===== PRICE ===== */
        if (price != null)
            a.setAvgVolumeWeek(round(AnalyticsMath.avgLastN(price.getVolume(), 5)));

        /* ===== OPTIONS ===== */
        if (opt != null) {
            a.setSupport(round(opt.getSupport()));
            a.setResistance(round(opt.getResistance()));
            a.setCallPutVolumeRatio(opt.getCallPutVolumeRatio());
            a.setCallPutOIRatio(opt.getCallPutOIRatio());
            a.setPcr(round(opt.getPcr()));
            a.setBias(opt.getBias());
        }

        /* ===== COMPANY ===== */
        if (co != null) {
            a.setSector(co.getSector());
            a.setIndustry(co.getIndustry());
            a.setMarketCap(co.getMarketCapitalization());
            a.setMarketCapCategory(marketCapCategory(co.getMarketCapitalization()));
            a.setAnalystRating(deriveAnalystRating(co));
            a.setPeRatio(round(co.getPeRatio()));
            a.setPriceToBook(round(co.getPriceToBookRatio()));
            a.setEps(round(co.getEps()));
            a.setPercentInsiders(round(co.getPercentInsiders()));
            a.setPercentInstitutions(round(co.getPercentInstitutions()));
        }

        /* ===== GROWTH (Annual + Quarterly) ===== */
        if (inc != null) {
            a.setRevenueUpYears(AnalyticsMath.consecutiveUp(inc.getAnnualTotalRevenue()));
            a.setGrossProfitUpYears(AnalyticsMath.consecutiveUp(inc.getAnnualGrossProfit()));
            a.setNetIncomeUpYears(AnalyticsMath.consecutiveUp(inc.getAnnualNetIncome()));

            a.setRevenueUpQuarters(AnalyticsMath.consecutiveUp(inc.getQuarterlyTotalRevenue()));
            a.setGrossProfitUpQuarters(AnalyticsMath.consecutiveUp(inc.getQuarterlyGrossProfit()));
            a.setNetIncomeUpQuarters(AnalyticsMath.consecutiveUp(inc.getQuarterlyNetIncome()));
        }

        /* ===== BALANCE SHEET ===== */
        if (bal != null)
            a.setLiabilityEquityPercent(round(AnalyticsMath.liabilityEquityPercent(
                    bal.getAnnualTotalLiabilities(), bal.getAnnualTotalShareholderEquity())));

        /* ===== CASH FLOW ===== */
        if (cf != null) {
            a.setPositiveCashFlowYears(AnalyticsMath.positiveCount(cf.getAnnualChangeInCash()));
            a.setPositiveCashFlowQuarters(AnalyticsMath.positiveCount(cf.getQuarterlyChangeInCash()));
        }

        /* ===== CORE SCORES ===== */
        double momentum = round(momentumScore(price));
        double fundamental = round(fundamentalScore(co, inc));
        double risk = round(riskScore(co, bal));
        double insiderScore = round(insiderScore(insider));
        double optionsScore = round(optionsScore(opt));
        double overall = round(overallScore(fundamental, momentum, optionsScore, insiderScore, risk));

        a.setMomentumScore(momentum);
        a.setFundamentalScore(fundamental);
        a.setRiskScore(risk);
        a.setInsiderScore(insiderScore);
        a.setOptionsScore(optionsScore);
        a.setOverallScore(overall);
        a.setRecommendation(recommendation(overall));

        /* ===== HEDGE FUND ENGINE ===== */
        double smartMoney = round(smartMoneyScore(price, insider, opt));
        double gamma = round(gammaExposureScore(opt));
        double volatility = round(volatilityScore(price));
        double liquidity = round(liquidityScore(price));
        double factor = round(factorScore(co));
        double alpha = round(hedgeFundAlpha(overall, smartMoney, gamma, volatility, liquidity, factor));

        a.setSmartMoneyScore(smartMoney);
        a.setGammaExposureScore(gamma);
        a.setVolatilityScore(volatility);
        a.setLiquidityScore(liquidity);
        a.setFactorScore(factor);
        a.setHedgeFundAlpha(alpha);
        a.setGoodEntry(goodEntry(momentum, smartMoney, gamma));

        a.setUpdatedAt(LocalDateTime.now());
        analyticsRepo.save(a);
    }

    /* ================== ROUNDING UTILITY ================== */
    private double round(Double val) {
        if (val == null) return 0.0;
        return BigDecimal.valueOf(val)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /* ================= CORE ALGORITHMS ================= */
    private double momentumScore(StockPrice p) {
        if (p == null || p.getClose() == null || p.getClose().length < 20) return 50;
        double last = p.getClose()[p.getClose().length - 1];
        double prev = p.getClose()[p.getClose().length - 20];
        if (prev == 0) return 50;
        double change = (last - prev) / prev * 100;
        return Math.min(100, Math.max(0, (change + 20) * 2));
    }

    private double fundamentalScore(CompanyOverview co, IncomeStatement inc) {
        if (co == null || inc == null) return 50;
        int growth = AnalyticsMath.consecutiveUp(inc.getAnnualTotalRevenue());
        double score = growth * 10;
        if (co.getReturnOnEquityTTM() != null && co.getReturnOnEquityTTM() > 15) score += 20;
        if (co.getPeRatio() != null && co.getPeRatio() < 25) score += 20;
        return Math.min(100, score);
    }

    private double riskScore(CompanyOverview co, BalanceSheet bal) {
        double score = 100;
        if (bal != null) {
            double debt = AnalyticsMath.liabilityEquityPercent(
                    bal.getAnnualTotalLiabilities(), bal.getAnnualTotalShareholderEquity());
            if (debt > 200) score -= 40;
            else if (debt > 100) score -= 20;
        }
        if (co != null && co.getBeta() != null && co.getBeta() > 2) score -= 30;
        return Math.max(0, score);
    }

    private double insiderScore(InsiderTransaction t) {
        if (t == null || t.getTransactionTypes() == null) return 50;
        long buy = 0, sell = 0;
        for (int i = 0; i < t.getTransactionTypes().length; i++) {
            Long s = t.getSharesTransacted()[i];
            if (s == null) continue;
            if ("Purchase".equalsIgnoreCase(t.getTransactionTypes()[i])) buy += s;
            else sell += s;
        }
        long total = buy + sell;
        if (total == 0) return 50;
        return round((double) buy / total * 100);
    }

    private double optionsScore(OptionDashboard o) {
        if (o == null) return 50;
        double score = 50;
        if (o.getPcr() != null) {
            if (o.getPcr() < 0.8) score += 20;
            if (o.getPcr() > 1.3) score -= 20;
        }
        if ("BULLISH".equalsIgnoreCase(o.getBias())) score += 20;
        return Math.min(100, Math.max(0, round(score)));
    }

    private double overallScore(double f, double m, double o, double i, double r) {
        return round(f * 0.30 + m * 0.25 + o * 0.15 + i * 0.15 + r * 0.15);
    }

    private String recommendation(double s) {
        if (s > 80) return "STRONG BUY";
        if (s > 65) return "BUY";
        if (s > 50) return "HOLD";
        if (s > 35) return "SELL";
        return "STRONG SELL";
    }

    /* ===== HEDGE FUND ENGINE ===== */
    private double smartMoneyScore(StockPrice p, InsiderTransaction t, OptionDashboard o) {
        return round(momentumScore(p) * 0.5 + insiderScore(t) * 0.3 + optionsScore(o) * 0.2);
    }

    private double gammaExposureScore(OptionDashboard o) {
       /* if (o == null) return 50;
        long calls = o.getTotalCallOI(), puts = o.getTotalPutOI();
        if (calls + puts == 0) return 50;
        return round((double) calls / (calls + puts) * 100);*/
        return 0.0;
    }

    private double volatilityScore(StockPrice p) {
        if (p == null || p.getClose().length < 10) return 50;
        double avg = AnalyticsMath.avgLastN(p.getClose(), 10);
        double var = 0;
        for (int i = p.getClose().length - 10; i < p.getClose().length; i++)
            var += Math.pow(p.getClose()[i] - avg, 2);
        return Math.min(100, round(Math.sqrt(var / 10) * 10));
    }

    private double liquidityScore(StockPrice p) {
        if (p == null) return 50;
        double avg = AnalyticsMath.avgLastN(p.getVolume(), 20);
        if (avg > 50_000_000) return 100;
        if (avg > 10_000_000) return 80;
        if (avg > 2_000_000) return 60;
        if (avg > 500_000) return 40;
        return 20;
    }

    private double factorScore(CompanyOverview co) {
        if (co == null) return 50;
        double score = 50;
        if (co.getReturnOnEquityTTM() != null && co.getReturnOnEquityTTM() > 15) score += 20;
        if (co.getPeRatio() != null && co.getPeRatio() < 25) score += 15;
        return Math.min(100, round(score));
    }

    private double hedgeFundAlpha(double overall, double smart, double gamma, double vol, double liq, double factor) {
        return round(overall * 0.30 + smart * 0.20 + gamma * 0.10 + liq * 0.15 + factor * 0.15 + (100 - vol) * 0.10);
    }

    private boolean goodEntry(double momentum, double smart, double gamma) {
        return momentum > 60 && smart > 65 && gamma > 50;
    }

    /* ===== HELPERS ===== */
    private String marketCapCategory(Long cap) {
        if (cap == null) return "Unknown";
        if (cap < 50_000_000) return "Nano Cap";
        if (cap < 300_000_000) return "Micro Cap";
        if (cap < 2_000_000_000L) return "Small Cap";
        if (cap < 10_000_000_000L) return "Mid Cap";
        if (cap < 200_000_000_000L) return "Large Cap";
        return "Mega Cap";
    }

    private String deriveAnalystRating(CompanyOverview co) {
        int sb = safe(co.getAnalystRatingStrongBuy());
        int b = safe(co.getAnalystRatingBuy());
        int h = safe(co.getAnalystRatingHold());
        int s = safe(co.getAnalystRatingSell());
        int ss = safe(co.getAnalystRatingStrongSell());
        int max = Math.max(Math.max(sb, b), Math.max(h, Math.max(s, ss)));
        if (max == sb) return "Strong Buy";
        if (max == b) return "Buy";
        if (max == h) return "Hold";
        if (max == s) return "Sell";
        return "Strong Sell";
    }

    private int safe(Integer v) {
        return v == null ? 0 : v;
    }
}
