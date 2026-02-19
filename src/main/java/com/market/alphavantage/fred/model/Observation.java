package com.market.alphavantage.fred.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Observation {
    public String realtime_start;
    public String realtime_end;
    public String date;
    public String value;
}
