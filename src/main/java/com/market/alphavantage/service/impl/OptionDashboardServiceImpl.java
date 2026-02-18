package com.market.alphavantage.service;

import com.market.alphavantage.dto.*;
import com.market.alphavantage.entity.OptionDashboard;
import com.market.alphavantage.repository.OptionDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OptionDashboardServiceImpl {

    @Autowired
    public OptionDashboardRepository repository;

    /* ---------- MAIN DASHBOARD ---------- */
    public OptionDashboardResponseDTO getDashboard(String symbol) {

        List<OptionDashboard> all =
                repository.findBySymbolOrderByDateAsc(symbol);

        if (all.isEmpty())
            throw new RuntimeException("No options data found");

        OptionDashboardResponseDTO dto =
                new OptionDashboardResponseDTO();

        dto.setLatestDate(all.get(all.size() - 1).getDate());

        // NEW: expiration dropdown
        dto.setExpirationDates(
                repository.findDistinctDatesBySymbol(symbol)
        );

        dto.setBiasTrend(getBiasTrend(all));

        dto.setIvByExpiration(buildStrikeMap(all,
                OptionDashboard::getCallStrikePrice,
                OptionDashboard::getCallImpliedVolatility,
                OptionDashboard::getPutStrikePrice,
                OptionDashboard::getPutImpliedVolatility));

        dto.setOiByExpiration(buildStrikeMapLong(all,
                OptionDashboard::getCallStrikePrice,
                OptionDashboard::getCallOpenInterest,
                OptionDashboard::getPutStrikePrice,
                OptionDashboard::getPutOpenInterest));

        dto.setVolumeByExpiration(buildStrikeMapLong(all,
                OptionDashboard::getCallStrikePrice,
                OptionDashboard::getCallVolume,
                OptionDashboard::getPutStrikePrice,
                OptionDashboard::getPutVolume));

        dto.setPcrTrend(getMetric(all, OptionDashboard::getPcr));
        dto.setMaxPainTrend(getMetric(all, OptionDashboard::getMaxPain));
        dto.setResistanceTrend(getMetric(all, OptionDashboard::getResistance));
        dto.setSupportTrend(getMetric(all, OptionDashboard::getSupport));
        dto.setOiRatioTrend(getMetric(all, OptionDashboard::getCallPutOIRatio));
        dto.setVolumeRatioTrend(getMetric(all, OptionDashboard::getCallPutVolumeRatio));

        dto.setSignalsByExpiration(buildSignalsByExpiration(all));

        return dto;
    }

    /* ---------- STRIKE GRAPH DOUBLE ---------- */
    private Map<LocalDate, List<StrikeDataPointDTO>> buildStrikeMap(
            List<OptionDashboard> all,
            Function<OptionDashboard, Double[]> callStrikeFn,
            Function<OptionDashboard, Double[]> callValueFn,
            Function<OptionDashboard, Double[]> putStrikeFn,
            Function<OptionDashboard, Double[]> putValueFn) {

        Map<LocalDate, List<StrikeDataPointDTO>> result = new LinkedHashMap<>();

        for (OptionDashboard dash : all) {

            Double[] callStrike = callStrikeFn.apply(dash);
            Double[] callVal = callValueFn.apply(dash);
            Double[] putStrike = putStrikeFn.apply(dash);
            Double[] putVal = putValueFn.apply(dash);

            List<StrikeDataPointDTO> points = new ArrayList<>();

            for (int i = 0; i < safeLength(callStrike, callVal); i++) {
                points.add(new StrikeDataPointDTO(
                        callStrike[i],
                        callVal[i],
                        0.0));
            }

            for (int i = 0; i < safeLength(putStrike, putVal); i++) {
                points.add(new StrikeDataPointDTO(
                        putStrike[i],
                        0.0,
                        putVal[i]));
            }

            result.put(dash.getDate(), points);
        }

        return result;
    }

    /* ---------- STRIKE GRAPH LONG ---------- */
    private Map<LocalDate, List<StrikeDataPointDTO>> buildStrikeMapLong(
            List<OptionDashboard> all,
            Function<OptionDashboard, Double[]> strikeFn,
            Function<OptionDashboard, Long[]> callValueFn,
            Function<OptionDashboard, Double[]> strikePutFn,
            Function<OptionDashboard, Long[]> putValueFn) {

        Map<LocalDate, List<StrikeDataPointDTO>> result = new LinkedHashMap<>();

        for (OptionDashboard dash : all) {

            Double[] strikeCall = strikeFn.apply(dash);
            Long[] callVal = callValueFn.apply(dash);

            Double[] strikePut = strikePutFn.apply(dash);
            Long[] putVal = putValueFn.apply(dash);

            List<StrikeDataPointDTO> points = new ArrayList<>();

            for (int i = 0; i < safeLength(strikeCall, callVal); i++) {
                points.add(new StrikeDataPointDTO(
                        strikeCall[i],
                        callVal[i] == null ? null : callVal[i].doubleValue(),
                        null));
            }

            for (int i = 0; i < safeLength(strikePut, putVal); i++) {
                points.add(new StrikeDataPointDTO(
                        strikePut[i],
                        null,
                        putVal[i] == null ? null : putVal[i].doubleValue()));
            }

            result.put(dash.getDate(), points);
        }

        return result;
    }

    /* ---------- METRIC TREND ---------- */
    private List<DateValueDTO> getMetric(
            List<OptionDashboard> list,
            Function<OptionDashboard, Double> extractor) {

        List<DateValueDTO> result = new ArrayList<>();

        for (OptionDashboard dash : list) {
            result.add(new DateValueDTO(
                    dash.getDate(),
                    extractor.apply(dash)));
        }

        return result;
    }

    /* ---------- BIAS TREND ---------- */
    private List<DateValueDTO> getBiasTrend(
            List<OptionDashboard> list) {

        List<DateValueDTO> result = new ArrayList<>();

        for (OptionDashboard dash : list) {

            double val = "Bullish".equalsIgnoreCase(dash.getBias())
                    ? 1
                    : "Bearish".equalsIgnoreCase(dash.getBias())
                    ? -1 : 0;

            result.add(new DateValueDTO(dash.getDate(), val));
        }

        return result;
    }
    /* ---------- SIGNALS BY EXPIRATION ---------- */
    private Map<LocalDate, List<OptionSignalDTO>> buildSignalsByExpiration(List<OptionDashboard> all) {
        Map<LocalDate, List<OptionSignalDTO>> signalsMap = new LinkedHashMap<>();

        for (OptionDashboard dash : all) {
            signalsMap.put(dash.getDate(), buildSignals(dash));
        }

        return signalsMap;
    }



    /* ---------- SIGNAL ENGINE ---------- */
    private List<OptionSignalDTO> buildSignals(OptionDashboard dash) {

        List<OptionSignalDTO> signals = new ArrayList<>();

        signals.add(gammaExposureSignal(dash));
        signals.add(liquiditySignal(dash));
        signals.add(volumeSignal(dash));
        signals.add(oiSignal(dash));
        signals.add(pcrSignal(dash));
        signals.add(strikeRecommendation(dash));

        return signals;
    }

    private OptionSignalDTO gammaExposureSignal(OptionDashboard dash) {

        double callGex = exposure(
                dash.getCallGamma(),
                dash.getCallOpenInterest());

        double putGex = exposure(
                dash.getPutGamma(),
                dash.getPutOpenInterest());

        double net = callGex - putGex;

        return new OptionSignalDTO(
                "GAMMA_EXPOSURE",
                net > 0 ? "Positive Gamma" : "Negative Gamma",
                net);
    }

    private OptionSignalDTO liquiditySignal(OptionDashboard dash) {

        long totalVolume =
                sum(dash.getCallVolume()) +
                        sum(dash.getPutVolume());

        return new OptionSignalDTO(
                "LIQUIDITY",
                totalVolume > 50000 ? "High Liquidity" :
                        totalVolume > 10000 ? "Medium Liquidity" :
                                "Low Liquidity",
                (double) totalVolume);
    }

    private OptionSignalDTO volumeSignal(OptionDashboard dash) {

        long callVol = sum(dash.getCallVolume());
        long putVol = sum(dash.getPutVolume());

        return new OptionSignalDTO(
                "VOLUME_PRESSURE",
                callVol > putVol ?
                        "Call buying pressure" :
                        "Put buying pressure",
                (double) (callVol - putVol));
    }

    private OptionSignalDTO oiSignal(OptionDashboard dash) {

        long callOI = sum(dash.getCallOpenInterest());
        long putOI = sum(dash.getPutOpenInterest());

        return new OptionSignalDTO(
                "OI_BUILDUP",
                callOI > putOI ?
                        "Bullish OI build-up" :
                        "Bearish OI build-up",
                (double) (callOI - putOI));
    }

    private OptionSignalDTO pcrSignal(OptionDashboard dash) {

        double pcr = dash.getPcr() == null ? 1 : dash.getPcr();

        return new OptionSignalDTO(
                "PCR_SIGNAL",
                pcr > 1.2 ? "Bearish Extreme" :
                        pcr < 0.7 ? "Bullish Extreme" :
                                "Neutral",
                pcr);
    }

    private OptionSignalDTO strikeRecommendation(OptionDashboard dash) {

        double strike =
                "Bullish".equalsIgnoreCase(dash.getBias())
                        ? dash.getResistance()
                        : dash.getSupport();

        return new OptionSignalDTO(
                "SMART_STRIKE",
                "Recommended strike",
                strike);
    }

    /* ---------- HELPERS ---------- */
    private double exposure(Double[] gamma, Long[] oi) {
        if (gamma == null || oi == null) return 0;

        double sum = 0;
        for (int i = 0; i < Math.min(gamma.length, oi.length); i++)
            sum += gamma[i] * oi[i];

        return sum;
    }

    private long sum(Long[] arr) {
        if (arr == null) return 0;
        long s = 0;
        for (Long v : arr) if (v != null) s += v;
        return s;
    }

    private int safeLength(Object[] a, Object[] b) {
        if (a == null || b == null) return 0;
        return Math.min(a.length, b.length);
    }
}
