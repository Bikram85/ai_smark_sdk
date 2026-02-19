package com.market.alphavantage.fred.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Root {
    public String realtime_start;
    public String realtime_end;
    public String observation_start;
    public String observation_end;
    public String units;
    public int output_type;
    public String file_type;
    public String order_by;
    public String sort_order;
    public int count;
    public int offset;
    public int limit;
    public ArrayList<Observation> observations;
}
