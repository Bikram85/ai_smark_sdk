package com.market.alphavantage.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "index_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexPrice {

    @Id
    @Column(length = 20)
    private String symbol;

    private String country;

    private String name;

    @Column(columnDefinition = "text[]")
    private String[] dates;

    @Column(columnDefinition = "double precision[]")
    private Double[] open;

    @Column(columnDefinition = "double precision[]")
    private Double[] high;

    @Column(columnDefinition = "double precision[]")
    private Double[] low;

    @Column(columnDefinition = "double precision[]")
    private Double[] close;

    @Column(columnDefinition = "bigint[]")
    private Long[] volume;
}
