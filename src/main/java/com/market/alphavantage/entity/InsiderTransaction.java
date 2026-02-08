package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "insider_transactions")
@Data
public class InsiderTransaction {

    @Id
    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "transaction_dates", columnDefinition = "date[]")
    private LocalDate[] transactionDates;

    @Column(name = "insider_names", columnDefinition = "text[]")
    private String[] insiderNames;

    @Column(name = "relationships", columnDefinition = "text[]")
    private String[] relationships;

    @Column(name = "transaction_types", columnDefinition = "text[]")
    private String[] transactionTypes;

    @Column(name = "ownership_types", columnDefinition = "text[]")
    private String[] ownershipTypes;

    @Column(name = "shares_transacted", columnDefinition = "bigint[]")
    private Long[] sharesTransacted;

    @Column(name = "shares_owned", columnDefinition = "bigint[]")
    private Long[] sharesOwned;

    @Column(name = "avg_prices", columnDefinition = "double precision[]")
    private Double[] avgPrices;

    @Column(name = "reported_titles", columnDefinition = "text[]")
    private String[] reportedTitles;
}
