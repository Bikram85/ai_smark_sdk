package com.market.alphavantage.dto;

import com.market.alphavantage.entity.EarningsCalendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarningsCalendarDTO {

    private List<EarningsCalendar> earnings;
}


