package com.market.alphavantage.govt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Links {
    public String self;
    public String first;
    public Object prev;
    public String next;
    public String last;
}
