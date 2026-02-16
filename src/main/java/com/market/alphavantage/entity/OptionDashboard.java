package com.market.alphavantage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "option_dashboard")
public class OptionDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String contractId;

    private LocalDate date;


    // Call data as arrays
    @Column(columnDefinition = "BIGINT[]")
    private Long[] callOpenInterest;

    @Column(columnDefinition = "double precision[]")
    private Double[] callStrikePrice;

    @Column(columnDefinition = "BIGINT[]")
    private Long[] callVolume;

    @Column(columnDefinition = "double precision[]")
    private Double[] callImpliedVolatility;

    @Column(columnDefinition = "double precision[]")
    private Double[] callDelta;

    @Column(columnDefinition = "double precision[]")
    private Double[] callGamma;

    @Column(columnDefinition = "double precision[]")
    private Double[] callTheta;

    @Column(columnDefinition = "double precision[]")
    private Double[] callVega;

    @Column(columnDefinition = "double precision[]")
    private Double[] callRho;

    // Put data as arrays
    @Column(columnDefinition = "BIGINT[]")
    private Long[] putOpenInterest;

    @Column(columnDefinition = "double precision[]")
    private Double[] putStrikePrice;

    @Column(columnDefinition = "BIGINT[]")
    private Long[] putVolume;

    @Column(columnDefinition = "double precision[]")
    private Double[] putImpliedVolatility;

    @Column(columnDefinition = "double precision[]")
    private Double[] putDelta;

    @Column(columnDefinition = "double precision[]")
    private Double[] putGamma;

    @Column(columnDefinition = "double precision[]")
    private Double[] putTheta;

    @Column(columnDefinition = "double precision[]")
    private Double[] putVega;

    @Column(columnDefinition = "double precision[]")
    private Double[] putRho;

    // Metrics
    private Double maxPain;
    private Double resistance;
    private Double support;
    private Double callPutVolumeRatio;
    private Double callPutOIRatio;
    private Double pcr;
    private String bias;
}
