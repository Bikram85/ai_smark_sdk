package com.market.alphavantage.util;

import com.market.alphavantage.dto.AnalyticsDTO;
import com.market.alphavantage.entity.Analytics;

public class AnalyticsMapper {

    public static AnalyticsDTO toDTO(Analytics a) {

        return AnalyticsDTO.builder()
                .symbol(a.getSymbol())
                .exchange(a.getExchange())

                .avgVolumeWeek(a.getAvgVolumeWeek())

                .support(a.getSupport())
                .resistance(a.getResistance())
                .totalCallOI(a.getTotalCallOI())
                .totalPutOI(a.getTotalPutOI())
                .pcr(a.getPcr())
                .bias(a.getBias())

                .sector(a.getSector())
                .industry(a.getIndustry())
                .marketCap(a.getMarketCap())
                .marketCapCategory(a.getMarketCapCategory())
                .analystRating(a.getAnalystRating())

                .revenueUpYears(a.getRevenueUpYears())
                .liabilityEquityPercent(a.getLiabilityEquityPercent())
                .positiveCashFlowYears(a.getPositiveCashFlowYears())

                .momentumScore(a.getMomentumScore())
                .fundamentalScore(a.getFundamentalScore())
                .riskScore(a.getRiskScore())
                .insiderScore(a.getInsiderScore())
                .optionsScore(a.getOptionsScore())

                .overallScore(a.getOverallScore())
                .recommendation(a.getRecommendation())

                .smartMoneyScore(a.getSmartMoneyScore())
                .gammaExposureScore(a.getGammaExposureScore())
                .volatilityScore(a.getVolatilityScore())
                .liquidityScore(a.getLiquidityScore())
                .factorScore(a.getFactorScore())
                .hedgeFundAlpha(a.getHedgeFundAlpha())
                .goodEntry(a.getGoodEntry())

                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
