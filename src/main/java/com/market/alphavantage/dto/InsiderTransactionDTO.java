package com.market.alphavantage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsiderTransactionDTO {

    private String symbol;

    private List<LocalDate> transactionDate;
    private List<String> insiderName;
    private List<String> relationship;
    private List<String> transactionType;
    private List<String> ownershipType;
    private List<Long> sharesTransacted;
    private List<Long> sharesOwned;
    private List<Double> avgPrice;
    private List<String> reportedTitle;
}

