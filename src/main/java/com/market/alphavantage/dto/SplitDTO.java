package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplitDTO {

    private String symbol;
    private List<LocalDate> splitDate;
    private List<String> splitRatio;
}
