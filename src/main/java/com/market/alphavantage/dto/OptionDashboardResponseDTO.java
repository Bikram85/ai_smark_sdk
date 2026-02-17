package com.market.alphavantage.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OptionDashboardResponseDTO {

    private LocalDate latestDate;

    // Trends
    private List<DateValueDTO> biasTrend;
    private List<DateValueDTO> pcrTrend;
    private List<DateValueDTO> maxPainTrend;
    private List<DateValueDTO> resistanceTrend;
    private List<DateValueDTO> supportTrend;
    private List<DateValueDTO> oiRatioTrend;
    private List<DateValueDTO> volumeRatioTrend;

    // Strike-based graphs grouped by date
    private Map<LocalDate, List<StrikeDataPointDTO>> ivByExpiration;
    private Map<LocalDate, List<StrikeDataPointDTO>> oiByExpiration;
    private Map<LocalDate, List<StrikeDataPointDTO>> volumeByExpiration;

    // Signals engine output
    private List<OptionSignalDTO> signals;
}
