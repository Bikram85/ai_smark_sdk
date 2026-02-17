package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OptionSignalDTO {

    private String signalType;
    private String message;
    private Double score;
}
