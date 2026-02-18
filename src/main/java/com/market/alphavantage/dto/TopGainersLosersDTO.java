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
    private List<String> symbols;
    private List<String> names;
    private List<Double> prices;
    private List<Double> changes;
    private List<Double> percentChanges;
    private List<Long> volumes;
    private List<String> marketCapCategory;

    // constructor, getters, setters
}



