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
import java.util.Optional;

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

    /* ================= CORE SYMBOL PROCESSING ================= */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSymbol(Symbol sym) {
        String symbol = sym.getSymbol();
        Analytics a = new Analytics();
        a.setSymbol(symbol);
        a.setExchange(sym.getExchange());

        // Fetch data
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
        double momentum   = round(momentumScoreRSI(price));
        double fundamental = round(fundamentalScore(co, inc));
        double risk       = round(riskScore(co, bal));
        double insiderSc  = round(insiderScore(insider));
        double optionsSc  = round(optionsScore(opt, price));

        double overall = round(overallScore(fundamental, momentum, optionsSc, insiderSc, risk));

        a.setMomentumScore(momentum);
        a.setFundamentalScore(fundamental);
        a.setRiskScore(risk);
        a.setInsiderScore(insiderSc);
        a.setOptionsScore(optionsSc);
        a.setOverallScore(overall);
        a.setRecommendation(recommendation(overall));

        /* ===== HEDGE FUND ENGINE ===== */
        double smartMoney = round(smartMoneyScore(price, insider, opt));
        double gamma      = round(gammaExposureScore(opt, price));
        double volatility = round(volatilityScore(price));
        double liquidity  = round(liquidityScore(price));
        double alpha      = round(hedgeFundAlpha(overall, smartMoney, gamma, volatility, liquidity));

        a.setSmartMoneyScore(smartMoney);
        a.setGammaExposureScore(gamma);
        a.setVolatilityScore(volatility);
        a.setLiquidityScore(liquidity);
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

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    /* ================== CORE SCORES ================== */
    private double momentumScoreRSI(StockPrice p) {
        int period = 14;
        if (p == null || p.getClose() == null || p.getClose().length <= period) return 50;
        Double[] close = p.getClose();
        double gain = 0, loss = 0;
        for (int i = close.length - period; i < close.length; i++) {
            double change = close[i] - close[i - 1];
            if (change > 0) gain += change; else loss += -change;
        }
        double avgGain = gain / period;
        double avgLoss = loss / period;
        double rs = avgLoss == 0 ? 1e6 : avgGain / avgLoss;
        double rsi = 100 - (100 / (1 + rs));
        return round2(rsi);
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

    /* ================== OPTIONS ================== */
    private double optionsScore(OptionDashboard o, StockPrice price) {
        if (o == null) return 50;
        double score = 50;

        // PCR adjustment
        if (o.getPcr() != null) {
            double pcrEffect = (1 - o.getPcr()) * 50;
            score += pcrEffect;
        }

        // Bias adjustment
        if (o.getBias() != null) {
            switch(o.getBias().toUpperCase()) {
                case "EXTREME_GREED": score += 20; break;
                case "CALL_HEAVY":    score += 10; break;
                case "BALANCED":      score += 0;  break;
                case "PUT_HEAVY":     score -= 10; break;
                case "EXTREME_FEAR":  score -= 20; break;
            }
        }

        // Adjust if spot is near support/resistance
        if (price != null && price.getClose() != null && price.getClose().length > 0) {
            double spot = price.getClose()[price.getClose().length - 1];
            if (o.getSupport() != null && Math.abs(spot - o.getSupport()) / spot < 0.02) score += 5;
            if (o.getResistance() != null && Math.abs(o.getResistance() - spot) / spot < 0.02) score -= 5;
        }

        return Math.min(100, Math.max(0, round2(score)));
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

    /* ================== HEDGE FUND ENGINE ================== */
    private double smartMoneyScore(StockPrice p, InsiderTransaction t, OptionDashboard o) {
        return round(momentumScoreRSI(p) * 0.5 + insiderScore(t) * 0.3 + optionsScore(o, p) * 0.2);
    }

    private double gammaExposureScore(OptionDashboard o, StockPrice price) {
        if (o == null || price == null || price.getClose() == null || price.getClose().length == 0) return 50;
        double spot = price.getClose()[price.getClose().length - 1];

        double netGEX = 0;
        if (o.getCallOpenInterest() != null && o.getCallGamma() != null && o.getCallStrikePrice() != null) {
            for (int i = 0; i < o.getCallOpenInterest().length; i++) {
                if (o.getCallOpenInterest()[i] != null && o.getCallGamma()[i] != null && o.getCallStrikePrice()[i] != null) {
                    double distance = Math.max(0, o.getCallStrikePrice()[i] - spot);
                    netGEX += o.getCallGamma()[i] * o.getCallOpenInterest()[i] / (distance + 1);
                }
            }
        }

        if (o.getPutOpenInterest() != null && o.getPutGamma() != null && o.getPutStrikePrice() != null) {
            for (int i = 0; i < o.getPutOpenInterest().length; i++) {
                if (o.getPutOpenInterest()[i] != null && o.getPutGamma()[i] != null && o.getPutStrikePrice()[i] != null) {
                    double distance = Math.max(0, spot - o.getPutStrikePrice()[i]);
                    netGEX -= o.getPutGamma()[i] * o.getPutOpenInterest()[i] / (distance + 1);
                }
            }
        }

        double score = 50 + (netGEX / (Math.abs(netGEX) + 1) * 50);
        return Math.max(0, Math.min(100, round2(score)));
    }

    private double volatilityScore(StockPrice p) {
        if (p == null || p.getClose().length < 10) return 50;
        double avg = AnalyticsMath.avgLastN(p.getClose(), 10);
        double var = 0;
        for (int i = p.getClose().length - 10; i < p.getClose().length; i++)
            var += Math.pow(p.getClose()[i] - avg, 2);
        double stdDev = Math.sqrt(var / 10);
        return round2(50 + Math.min(50, (stdDev / 5) * 50));
    }

    private double liquidityScore(StockPrice p) {
        if (p == null) return 50;
        if (p.getClose() == null || p.getVolume() == null) return 50;
        double avgDollarVolume = AnalyticsMath.avgLastN(p.getVolume(), 20) *
                p.getClose()[p.getClose().length - 1];
        if (avgDollarVolume > 50_000_000) return 100;
        if (avgDollarVolume > 10_000_000) return 80;
        if (avgDollarVolume > 2_000_000) return 60;
        if (avgDollarVolume > 500_000) return 40;
        return 20;
    }

    private double hedgeFundAlpha(double overall, double smart, double gamma, double vol, double liq) {
        return round(overall * 0.30 + smart * 0.20 + gamma * 0.10 + liq * 0.15 + (100 - vol) * 0.10);
    }

    private boolean goodEntry(double momentum, double smart, double gamma) {
        return momentum > 60 && smart > 65 && gamma > 50;
    }

    /* ================== HELPERS ================== */
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