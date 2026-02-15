package com.market.alphavantage.service;

import com.market.alphavantage.dto.EarningsCalendarDTO;

public interface EarningsCalendarService {

    void loadEarningsCalendar();

    EarningsCalendarDTO getEarningsCalendar();

    EarningsCalendarDTO loadEarningsCalendar(String horizon);
}

