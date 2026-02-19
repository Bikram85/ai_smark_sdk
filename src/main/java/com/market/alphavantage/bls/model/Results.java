package com.market.alphavantage.bls.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Results {
    public ArrayList<Series> series;
}
