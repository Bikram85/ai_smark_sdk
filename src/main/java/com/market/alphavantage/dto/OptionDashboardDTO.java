package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionDashboardDTO {

    private String symbol;

    private Double supportStrike;
    private Double resistanceStrike;

    private Long totalCallOI;
    private Long totalPutOI;

    private Double putCallRatio;

    private String marketBias;
}
