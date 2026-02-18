package com.market.alphavantage.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public class IndexConstants {

    private final String symbol;      // ETF symbol
    private final String country;     // Country code
    private final String name;        // Friendly name / description

    // Popular indices represented by ETFs for easy tracking
    public static final List<IndexConstants> POPULAR_INDICES = Arrays.asList(
            // United States
            new IndexConstants("SPY", "US", "S&P 500"),
            new IndexConstants("QQQ", "US", "NASDAQ-100"),
            new IndexConstants("DIA", "US", "Dow Jones"),

            // Japan
            new IndexConstants("EWJ", "JP", "Japan ETF"),

            // India
            new IndexConstants("INDA", "IN", "India ETF"),

            // United Kingdom
            new IndexConstants("EWU", "UK", "UK ETF"),

            // Germany
            new IndexConstants("EWG", "DE", "Germany ETF"),

            // China
            new IndexConstants("FXI", "CN", "China ETF"),

            // Hong Kong
            new IndexConstants("EWH", "HK", "Hong Kong ETF"),

            // Canada
            new IndexConstants("EWC", "CA", "Canada ETF"),

            // Korea
            new IndexConstants("EWY", "KR", "Korea ETF"),

            // Brazil
            new IndexConstants("EWZ", "BR", "Brazil ETF")
    );
}
