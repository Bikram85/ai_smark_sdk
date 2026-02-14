package com.market.alphavantage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "option_dashboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;
    @Column
    private Double support;
    @Column
    private Double resistance;

    @Column
    private Long totalCallOI;

    @Column
    private Long totalPutOI;
    @Column
    private Double pcr;
    @Column
    private String bias;

}
