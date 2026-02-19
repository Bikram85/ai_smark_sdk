package com.market.alphavantage.bls.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Datum {
    public String year;
    public String period;
    public String periodName;
    public String latest;
    public String value;
    public ArrayList<Footnote> footnotes;
}
