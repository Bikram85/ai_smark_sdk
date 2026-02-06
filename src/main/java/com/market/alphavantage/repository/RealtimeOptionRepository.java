package com.market.alphavantage.repository;

import com.market.alphavantage.entity.RealtimeOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealtimeOptionRepository extends JpaRepository<RealtimeOption, String> {}
