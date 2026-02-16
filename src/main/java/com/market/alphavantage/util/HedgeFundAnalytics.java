package com.market.alphavantage.util;

import com.market.alphavantage.entity.*;

public class HedgeFundAnalytics {

    private double smartMoneyScore(
            StockPrice p,
            InsiderTransaction insider,
            OptionDashboard opt) {

        double momentum = momentumScore(p);
        double insiderScore = insiderScore(insider);
        double optionScore = optionsScore(opt);

        return momentum * 0.5 +
                insiderScore * 0.3 +
                optionScore * 0.2;
    }

    private double gammaExposureScore(OptionDashboard opt) {

      /*  if (opt == null) return 50;

        long calls = opt.getTotalCallOI();
        long puts = opt.getTotalPutOI();

        if (calls + puts == 0) return 50;

        double ratio = (double) calls / (calls + puts);

        return ratio * 100;*/
        return 0.0;
    }

    private double volatilityScore(StockPrice p) {

        Double[] prices = p.getClose();

        if (prices.length < 10) return 50;

        double avg = AnalyticsMath.avgLastN(prices, 10);

        double var = 0;
        for (int i = prices.length - 10; i < prices.length; i++) {
            var += Math.pow(prices[i] - avg, 2);
        }

        double vol = Math.sqrt(var / 10);

        return Math.min(100, vol * 10);
    }

    private double liquidityScore(StockPrice p) {

        double avgVol =
                AnalyticsMath.avgLastN(p.getVolume(), 20);

        if (avgVol > 50_000_000) return 100;
        if (avgVol > 10_000_000) return 80;
        if (avgVol > 2_000_000) return 60;
        if (avgVol > 500_000) return 40;

        return 20;
    }

    private double factorScore(CompanyOverview co) {

        double score = 50;

        if (co.getReturnOnEquityTTM() != null &&
                co.getReturnOnEquityTTM() > 15)
            score += 20;

        if (co.getPeRatio() != null &&
                co.getPeRatio() < 25)
            score += 15;

        return Math.min(100, score);
    }

    private double hedgeFundAlpha(
            double overall,
            double smartMoney,
            double gamma,
            double vol,
            double liquidity,
            double factor) {

        return overall * 0.30 +
                smartMoney * 0.20 +
                gamma * 0.10 +
                liquidity * 0.15 +
                factor * 0.15 +
                (100 - vol) * 0.10;
    }

    private boolean goodEntry(
            double momentum,
            double smartMoney,
            double gamma) {

        return momentum > 60 &&
                smartMoney > 65 &&
                gamma > 50;
    }


    private double momentumScore(StockPrice p) {

        if (p == null || p.getClose() == null) return 0;

        Double[] prices = p.getClose();
        Double[] volumes = p.getVolume();

        if (prices.length < 20) return 0;

        double last = prices[prices.length - 1];
        double prev20 = prices[prices.length - 20];

        if (prev20 == 0) return 0;

        double priceChange = (last - prev20) / prev20 * 100;

        double volRecent =
                AnalyticsMath.avgLastN(volumes, 5);

        double volOld =
                AnalyticsMath.avgLastN(volumes, 20);

        double volumeBoost =
                volRecent > volOld ? 10 : 0;

        double score =
                Math.min(Math.max(priceChange, -20), 20) + 20;

        return Math.min(100, Math.max(0,
                score * 2 + volumeBoost));
    }
    private double fundamentalScore(
            CompanyOverview co,
            IncomeStatement inc) {

        if (co == null || inc == null) return 50;

        int growthScore =
                AnalyticsMath.consecutiveUp(
                        inc.getAnnualTotalRevenue()) * 10;

        double peScore = 0;
        if (co.getPeRatio() != null) {
            double pe = co.getPeRatio();
            peScore = pe < 25 ? 20 : 5;
        }

        double roeScore = 0;
        if (co.getReturnOnEquityTTM() != null) {
            roeScore =
                    co.getReturnOnEquityTTM() > 15 ? 20 : 5;
        }

        return Math.min(100,
                growthScore + peScore + roeScore);
    }
    private double riskScore(
            CompanyOverview co,
            BalanceSheet bal) {

        double score = 100;

        if (bal != null) {
            double debtRatio =
                    AnalyticsMath.liabilityEquityPercent(
                            bal.getAnnualTotalLiabilities(),
                            bal.getAnnualTotalShareholderEquity());

            if (debtRatio > 200) score -= 40;
            else if (debtRatio > 100) score -= 20;
        }

        if (co != null && co.getBeta() != null) {
            double beta = co.getBeta();
            if (beta > 1.5) score -= 20;
            if (beta > 2) score -= 30;
        }

        return Math.max(0, score);
    }
    private double insiderScore(InsiderTransaction t) {

        if (t == null ||
                t.getTransactionTypes() == null)
            return 50;

        String[] types = t.getTransactionTypes();
        Long[] shares = t.getSharesTransacted();

        long buy = 0;
        long sell = 0;

        for (int i = 0; i < types.length; i++) {
            if (shares[i] == null) continue;

            if ("Purchase".equalsIgnoreCase(types[i]))
                buy += shares[i];
            else
                sell += shares[i];
        }

        long total = buy + sell;
        if (total == 0) return 50;

        double ratio = (double) buy / total;

        return ratio * 100;
    }
    private double optionsScore(OptionDashboard o) {

        if (o == null) return 50;

        double score = 50;

        if (o.getPcr() != null) {
            double pcr = o.getPcr();

            if (pcr < 0.8) score += 20;
            else if (pcr > 1.3) score -= 20;
        }

        if ("BULLISH".equalsIgnoreCase(o.getBias()))
            score += 20;
        else if ("BEARISH".equalsIgnoreCase(o.getBias()))
            score -= 20;

        return Math.min(100, Math.max(0, score));
    }
    private double overallScore(
            double fundamental,
            double momentum,
            double options,
            double insider,
            double risk) {

        return
                fundamental * 0.30 +
                        momentum * 0.25 +
                        options * 0.15 +
                        insider * 0.15 +
                        risk * 0.15;
    }

    private String recommendation(double score) {

        if (score > 80) return "STRONG BUY";
        if (score > 65) return "BUY";
        if (score > 50) return "HOLD";
        if (score > 35) return "SELL";

        return "STRONG SELL";
    }




}
