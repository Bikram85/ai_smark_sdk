package com.market.alphavantage.repository;


import com.market.alphavantage.entity.IpoCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpoCalendarRepository extends JpaRepository<IpoCalendar, String> {
}

