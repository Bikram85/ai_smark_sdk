package com.market.alphavantage.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "splits")
@Data
public class Split {

    @Id
    private String symbol;

    @ElementCollection
    private List<LocalDate> splitDate;

    @ElementCollection
    private List<String> splitRatio;
}
