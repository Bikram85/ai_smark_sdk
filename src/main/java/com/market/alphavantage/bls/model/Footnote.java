package com.market.alphavantage.bls.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Footnote {

    public String code;
    public String text;
}
