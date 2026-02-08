package com.market.alphavantage.repository;


import com.market.alphavantage.entity.EarningsCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EarningsCalendarRepository extends JpaRepository<EarningsCalendar, String> {

}

