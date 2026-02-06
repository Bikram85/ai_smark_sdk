package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "insider_transactions")
@Data
public class InsiderTransaction {

    @Id
    private String symbol;

    @ElementCollection
    private List<LocalDate> transactionDate;

    @ElementCollection
    private List<String> insiderName;

    @ElementCollection
    private List<String> relationship;

    @ElementCollection
    private List<String> transactionType;

    @ElementCollection
    private List<String> ownershipType;

    @ElementCollection
    private List<Long> sharesTransacted;

    @ElementCollection
    private List<Long> sharesOwned;

    @ElementCollection
    private List<Double> avgPrice;

    @ElementCollection
    private List<String> reportedTitle;
}
