package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsDTO {

    private String symbol;
    private String exchange;

    private Double avgVolumeWeek;

    /* Options */
    private Double support;
    private Double resistance;
    private Double callPutVolumeRatio;
    private Double callPutOIRatio;
    private Double pcr;
    private String bias;

    /* Company */
    private String sector;
    private String industry;
    private Long marketCap;
    private String marketCapCategory;
    private String analystRating;

    private Double peRatio; // <-- added
    private Double percentInsiders;
    private Double percentInstitutions;
    private Double eps;
    /* Growth - Years */
    private Integer revenueUpYears;
    private Integer grossProfitUpYears;  // <-- added
    private Integer netIncomeUpYears;    // <-- added

    /* Growth - Quarters */
    private Integer revenueUpQuarters;       // <-- added
    private Integer grossProfitUpQuarters;   // <-- added
    private Integer netIncomeUpQuarters;     // <-- added

    private Double liabilityEquityPercent;
    private Integer positiveCashFlowYears;
    private Integer positiveCashFlowQuarters; // <-- added

    /* Scores */
    private Double momentumScore;
    private Double fundamentalScore;
    private Double riskScore;
    private Double insiderScore;
    private Double optionsScore;

    private Double overallScore;
    private String recommendation;

    /* Hedge Fund Metrics */
    private Double smartMoneyScore;
    private Double gammaExposureScore;
    private Double volatilityScore;
    private Double liquidityScore;
    private Double factorScore;
    private Double hedgeFundAlpha;
    private Boolean goodEntry;

    private LocalDateTime updatedAt;

    /* For frontend use */
    private List<Map<String, Object>> rows;   // All analytics rows
    private Map<String, List<String>> filterOptions; // Dropdown options for each field
}
