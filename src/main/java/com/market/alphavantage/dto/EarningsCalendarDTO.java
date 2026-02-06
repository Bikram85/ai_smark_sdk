package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarningsCalendarDTO {

    private String id;
    private List<String> symbol;
    private List<String> name;
    private List<LocalDate> reportDate;
    private List<LocalDate> fiscalDateEnding;
    private List<Double> estimate;
    private List<String> currency;
    private List<String> timeOfTheDay;
}

