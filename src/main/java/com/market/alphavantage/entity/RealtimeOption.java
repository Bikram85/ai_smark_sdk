package com.market.alphavantage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "realtime_options")
@Data
public class RealtimeOption {

    @Id
    private String symbol;

    /* ===== CALLS ===== */

    @Column(columnDefinition = "text[]")
    private String[] callExpirationDate;

    @Column(columnDefinition = "double precision[]")
    private Double[] callStrikePrice;

    @Column(columnDefinition = "double precision[]")
    private Double[] callLastPrice;

    @Column(columnDefinition = "double precision[]")
    private Double[] callBid;

    @Column(columnDefinition = "double precision[]")
    private Double[] callAsk;

    @Column(columnDefinition = "bigint[]")
    private Long[] callVolume;

    @Column(columnDefinition = "bigint[]")
    private Long[] callOpenInterest;

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

    /* ===== PUTS ===== */

    @Column(columnDefinition = "text[]")
    private String[] putExpirationDate;

    @Column(columnDefinition = "double precision[]")
    private Double[] putStrikePrice;

    @Column(columnDefinition = "double precision[]")
    private Double[] putLastPrice;

    @Column(columnDefinition = "double precision[]")
    private Double[] putBid;

    @Column(columnDefinition = "double precision[]")
    private Double[] putAsk;

    @Column(columnDefinition = "bigint[]")
    private Long[] putVolume;

    @Column(columnDefinition = "bigint[]")
    private Long[] putOpenInterest;

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
}
