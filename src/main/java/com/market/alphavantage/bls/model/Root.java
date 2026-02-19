package com.market.alphavantage.bls.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Root {
    public String status;
    public int responseTime;
    public ArrayList<Object> message;
    @JsonProperty("Results")
    public Results results;
}
