package com.market.alphavantage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DateValueDTO {

    private LocalDate date;
    private Double value;
}
