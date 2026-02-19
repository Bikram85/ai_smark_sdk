package com.market.alphavantage.bls.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catalog {
    public String series_title;
    public String series_id;
    public String seasonality;
    public String survey_name;
    public String survey_abbreviation;
    public String measure_data_type;
    public String area;
    public String area_type;
}
