package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpoCalendarDTO {

    private String id;
    private List<String> symbol;
    private List<String> name;
    private List<LocalDate> ipoDate;
    private List<String> price;
    private List<String> shares;
    private List<String> exchange;
    private List<String> currency;
}
