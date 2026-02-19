package com.market.alphavantage.govt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Root {
    public ArrayList<Datum> data;
    public Meta meta;
    public Links links;
}
