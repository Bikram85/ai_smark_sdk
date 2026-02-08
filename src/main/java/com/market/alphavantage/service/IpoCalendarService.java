package com.market.alphavantage.service;

import com.market.alphavantage.dto.IpoCalendarDTO;

public interface IpoCalendarService {

    void loadIpoCalendar();

    IpoCalendarDTO getIpoCalendar();
}
