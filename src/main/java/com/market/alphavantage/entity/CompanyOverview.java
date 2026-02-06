package com.market.alphavantage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "company_overview")
@Data
public class CompanyOverview {

    @Id
    private String symbol;

    private String assetType;
    private String name;

    @Column(length = 8000)
    private String description;

    private String cik;
    private String exchange;
    private String currency;
    private String country;
    private String sector;
    private String industry;

    @Column(length = 1000)
    private String address;

    private String officialSite;
    private String fiscalYearEnd;

    private LocalDate latestQuarter;

    private Long marketCapitalization;
    private Long ebitda;

    private Double peRatio;
    private Double pegRatio;
    private Double bookValue;

    private Double dividendPerShare;
    private Double dividendYield;

    private Double eps;
    private Double revenuePerShareTTM;
    private Double profitMargin;
    private Double operatingMarginTTM;
    private Double returnOnAssetsTTM;
    private Double returnOnEquityTTM;

    private Long revenueTTM;
    private Long grossProfitTTM;

    private Double dilutedEPSTTM;

    private Double quarterlyEarningsGrowthYOY;
    private Double quarterlyRevenueGrowthYOY;

    private Double analystTargetPrice;

    private Integer analystRatingStrongBuy;
    private Integer analystRatingBuy;
    private Integer analystRatingHold;
    private Integer analystRatingSell;
    private Integer analystRatingStrongSell;

    private Double trailingPE;
    private Double forwardPE;
    private Double priceToSalesRatioTTM;
    private Double priceToBookRatio;
    private Double evToRevenue;
    private Double evToEBITDA;

    private Double beta;

    private Double week52High;
    private Double week52Low;

    private Double movingAvg50Day;
    private Double movingAvg200Day;

    private Long sharesOutstanding;
    private Long sharesFloat;

    private Double percentInsiders;
    private Double percentInstitutions;

    private LocalDate dividendDate;
    private LocalDate exDividendDate;
}

