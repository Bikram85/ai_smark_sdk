package com.market.alphavantage.govt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    public int count;
    public Labels labels;
    public DataTypes dataTypes;
    public DataFormats dataFormats;
    public int totalcount;
    public int totalpages;
}
