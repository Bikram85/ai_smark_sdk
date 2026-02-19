package com.market.alphavantage.dto;



import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonDataDTO {

    private String symbol;
    private String country;

    private String name;
    private List<String> dates;

    private List<Double> open;
    private List<Double> high;
    private List<Double> low;
    private List<Double> close;

    private List<Long> volume;
}

