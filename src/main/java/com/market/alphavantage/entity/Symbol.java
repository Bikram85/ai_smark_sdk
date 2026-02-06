package com.market.alphavantage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Symbol {


    @Id
    private String symbol;
    private String name;
    private String exchange;
    private String assetType;
}
