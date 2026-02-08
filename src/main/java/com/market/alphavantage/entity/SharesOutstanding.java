package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "shares_outstanding")
@Data
public class SharesOutstanding {

    @Id
    private String symbol;

    @Column(name = "fiscal_dates", columnDefinition = "date[]")
    private LocalDate[] fiscalDates;

    @Column(name = "reported_shares", columnDefinition = "bigint[]")
    private Long[] reportedShares;
}
