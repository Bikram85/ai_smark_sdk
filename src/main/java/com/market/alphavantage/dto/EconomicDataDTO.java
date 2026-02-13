package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EconomicDataDTO {
    private String symbol;
    private String name;
    private String interval;
    private LocalDate[] dates;
    private Double[] values;
}
