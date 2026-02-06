package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopGainersLosersDTO {

    private String id;

    private List<String> symbol;
    private List<String> name;
    private List<Double> price;
    private List<Double> change;
    private List<Double> percentChange;
    private List<Long> volume;
}

