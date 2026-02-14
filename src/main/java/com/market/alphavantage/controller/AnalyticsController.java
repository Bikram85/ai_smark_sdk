package com.market.alphavantage.controller;


import com.market.alphavantage.dto.AnalyticsDTO;
import com.market.alphavantage.entity.Analytics;
import com.market.alphavantage.repository.AnalyticsRepository;
import com.market.alphavantage.util.AnalyticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin
public class AnalyticsController {

    private final AnalyticsRepository analyticsRepo;

    /* ===============================
       GET ALL ANALYTICS
    =============================== */
    @GetMapping("/data")
    public List<AnalyticsDTO> getAnalyticsData() {

        List<Analytics> analyticsList = analyticsRepo.findAll();

        // Convert each Analytics entity to DTO
        List<AnalyticsDTO> rows = analyticsList.stream().map(a -> AnalyticsDTO.builder()
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
                .grossProfitUpYears(a.getGrossProfitUpYears())
                .netIncomeUpYears(a.getNetIncomeUpYears())
                .revenueUpQuarters(a.getRevenueUpQuarters())
                .grossProfitUpQuarters(a.getGrossProfitUpQuarters())
                .netIncomeUpQuarters(a.getNetIncomeUpQuarters())
                .liabilityEquityPercent(a.getLiabilityEquityPercent())
                .positiveCashFlowYears(a.getPositiveCashFlowYears())
                .positiveCashFlowQuarters(a.getPositiveCashFlowQuarters())
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
                .build()
        ).toList();

        return rows;
    }

    @GetMapping("/filters")
    public Map<String, List<String>> getAnalyticsFilters() {

        List<Analytics> analyticsList = analyticsRepo.findAll();

        // Convert each Analytics entity to a Map<String, String> including all filterable fields
        List<Map<String, String>> rows = analyticsList.stream().map(a -> Map.ofEntries(
                Map.entry("symbol", a.getSymbol()),
                Map.entry("exchange", a.getExchange()),
                Map.entry("sector", a.getSector()),
                Map.entry("industry", a.getIndustry()),
                Map.entry("marketCapCategory", a.getMarketCapCategory()),
                Map.entry("analystRating", a.getAnalystRating()),
                Map.entry("recommendation", a.getRecommendation()),
                Map.entry("bias", a.getBias()),
                Map.entry("goodEntry", a.getGoodEntry() != null ? a.getGoodEntry().toString() : null),
                Map.entry("peRatio", a.getPeRatio() != null ? a.getPeRatio().toString() : null),
                Map.entry("priceToBook", a.getPriceToBook() != null ? a.getPriceToBook().toString() : null),
                Map.entry("avgVolumeWeek", a.getAvgVolumeWeek() != null ? a.getAvgVolumeWeek().toString() : null),
                Map.entry("pcr", a.getPcr() != null ? a.getPcr().toString() : null),
                Map.entry("revenueUpYears", a.getRevenueUpYears() != null ? a.getRevenueUpYears().toString() : null),
                Map.entry("grossProfitUpYears", a.getGrossProfitUpYears() != null ? a.getGrossProfitUpYears().toString() : null),
                Map.entry("netIncomeUpYears", a.getNetIncomeUpYears() != null ? a.getNetIncomeUpYears().toString() : null),
                Map.entry("revenueUpQuarters", a.getRevenueUpQuarters() != null ? a.getRevenueUpQuarters().toString() : null),
                Map.entry("grossProfitUpQuarters", a.getGrossProfitUpQuarters() != null ? a.getGrossProfitUpQuarters().toString() : null),
                Map.entry("netIncomeUpQuarters", a.getNetIncomeUpQuarters() != null ? a.getNetIncomeUpQuarters().toString() : null),
                Map.entry("liabilityEquityPercent", a.getLiabilityEquityPercent() != null ? a.getLiabilityEquityPercent().toString() : null),
                Map.entry("positiveCashFlowYears", a.getPositiveCashFlowYears() != null ? a.getPositiveCashFlowYears().toString() : null),
                Map.entry("positiveCashFlowQuarters", a.getPositiveCashFlowQuarters() != null ? a.getPositiveCashFlowQuarters().toString() : null)
        )).toList();

        Map<String, List<String>> filterOptions = new HashMap<>();

        if (!rows.isEmpty()) {
            rows.get(0).keySet().forEach(key -> {
                List<String> options = rows.stream()
                        .map(r -> r.get(key))
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .toList();
                filterOptions.put(key, options);
            });
        }

        return filterOptions;
    }



    /* ===============================
       GET BY SYMBOL
    =============================== */
    @GetMapping("/{symbol}")
    public AnalyticsDTO getBySymbol(
            @PathVariable String symbol) {

        Analytics analytics =
                analyticsRepo.findById(symbol)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Analytics not found"));

        return AnalyticsMapper.toDTO(analytics);
    }


}

