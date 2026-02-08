package com.market.alphavantage.dto;

import com.market.alphavantage.entity.IpoCalendar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpoCalendarDTO {

    private List<IpoCalendar> ipoCalendar;
}
