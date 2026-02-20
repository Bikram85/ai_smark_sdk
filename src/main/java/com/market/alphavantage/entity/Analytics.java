package com.market.alphavantage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Getter
@Setter
public class Analytics {

    @Id
    private String symbol;

    private String exchange;

    /* ===== Volume ===== */
    private Double avgVolumeWeek;

    /* ===== REALTIME PRICE ===== */
    private Double closePrice;
    private Double volume;
    private Double previousClose;
    private Double changeAmount;
    private Double changePercent;

    private Double extendedHoursPrice;
    private Double extendedHoursChange;
    private Double extendedHoursChangePercent;

    /* ===== Options ===== */
    private Double support;
    private Double resistance;
    private Double CallPutVolumeRatio;
    private Double CallPutOIRatio;
    private Double pcr;
    private String bias;

    /* ===== Company ===== */
    private String sector;
    private String industry;
    private Long marketCap;
    private Double peRatio;
    private String marketCapCategory;
    private String analystRating;
    private Double priceToBook;
    private Double percentInsiders;
    private Double percentInstitutions;
    private Double eps;


    /* ===== Growth Trends (Annual) ===== */
    private Integer revenueUpYears;
    private Integer grossProfitUpYears;
    private Integer netIncomeUpYears;

    /* ===== Growth Trends (Quarterly) ===== */
    private Integer revenueUpQuarters;
    private Integer grossProfitUpQuarters;
    private Integer netIncomeUpQuarters;

    /* ===== Balance Sheet ===== */
    private Double liabilityEquityPercent;

    /* ===== Cash Flow ===== */
    private Integer positiveCashFlowYears;
    private Integer positiveCashFlowQuarters;

    /* ===== CORE SCORES ===== */
    private Double momentumScore;
    private Double fundamentalScore;
    private Double riskScore;
    private Double insiderScore;
    private Double optionsScore;

    private Double overallScore;
    private String recommendation;

    /* ===== HEDGE FUND ENGINE ===== */
    private Double smartMoneyScore;
    private Double gammaExposureScore;
    private Double volatilityScore;
    private Double liquidityScore;
    private Double factorScore;
    private Double hedgeFundAlpha;
    private Boolean goodEntry;

    private LocalDateTime updatedAt;
}
