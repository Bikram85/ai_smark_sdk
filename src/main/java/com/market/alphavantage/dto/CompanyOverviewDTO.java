package com.market.alphavantage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyOverviewDTO {

    private String symbol;
    private String assetType;
    private String name;
    private String description;
    private String exchange;
    private String currency;
    private String country;
    private String sector;
    private String industry;

    private Long marketCapitalization;
    private Double peRatio;
    private Double pegRatio;
    private Double eps;

    private Double dividendYield;
    private Double analystTargetPrice;

    private Double week52High;
    private Double week52Low;

    private Long sharesOutstanding;
}
