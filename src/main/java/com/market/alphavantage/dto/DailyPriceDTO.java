package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyPriceDTO {
    private String symbol;
    private LocalDate date;
    private Double close;
}
