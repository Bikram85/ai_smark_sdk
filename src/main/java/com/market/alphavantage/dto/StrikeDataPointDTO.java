package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StrikeDataPointDTO {

    private Double strike;
    private Double callValue;
    private Double putValue;
}
