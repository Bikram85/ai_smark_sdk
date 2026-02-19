package com.market.alphavantage.service.impl.processor;

import com.market.alphavantage.entity.OptionDashboard;
import com.market.alphavantage.repository.OptionDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionProcessor {

    private final OptionDashboardRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fetchDetails(String symbol) {

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String url = baseUrl
                + "?function=HISTORICAL_OPTIONS"
                + "&symbol=" + symbol
                + "&require_greeks=true"
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        List<Map<String, Object>> optionData =
                (List<Map<String, Object>>) response.get("data");
        if (optionData == null || optionData.isEmpty()) return;

        Map<String, List<Map<String, Object>>> optionsByExpiration =
                optionData.stream()
                        .collect(Collectors.groupingBy(opt -> (String) opt.get("expiration")));

        for (String expiration : optionsByExpiration.keySet()) {

            List<Map<String, Object>> optionsForExp = optionsByExpiration.get(expiration);
            if (optionsForExp.isEmpty()) continue;

            String contractId = (String) optionsForExp.get(0).get("contractID");

            List<Long> callOIList = new ArrayList<>();
            List<Double> callStrikeList = new ArrayList<>();
            List<Long> callVolumeList = new ArrayList<>();
            List<Double> callIVList = new ArrayList<>();
            List<Double> callDeltaList = new ArrayList<>();
            List<Double> callGammaList = new ArrayList<>();
            List<Double> callThetaList = new ArrayList<>();
            List<Double> callVegaList = new ArrayList<>();
            List<Double> callRhoList = new ArrayList<>();

            List<Long> putOIList = new ArrayList<>();
            List<Double> putStrikeList = new ArrayList<>();
            List<Long> putVolumeList = new ArrayList<>();
            List<Double> putIVList = new ArrayList<>();
            List<Double> putDeltaList = new ArrayList<>();
            List<Double> putGammaList = new ArrayList<>();
            List<Double> putThetaList = new ArrayList<>();
            List<Double> putVegaList = new ArrayList<>();
            List<Double> putRhoList = new ArrayList<>();

            for (Map<String, Object> opt : optionsForExp) {

                String type = (String) opt.get("type");

                if ("call".equalsIgnoreCase(type)) {

                    callOIList.add(Long.parseLong(opt.get("open_interest").toString()));
                    callStrikeList.add(round2(parseDouble(opt.get("strike"))));
                    callVolumeList.add(Long.parseLong(opt.get("volume").toString()));
                    callIVList.add(round2(parseDouble(opt.get("implied_volatility"))));
                    callDeltaList.add(round2(parseDouble(opt.get("delta"))));
                    callGammaList.add(round2(parseDouble(opt.get("gamma"))));
                    callThetaList.add(round2(parseDouble(opt.get("theta"))));
                    callVegaList.add(round2(parseDouble(opt.get("vega"))));
                    callRhoList.add(round2(parseDouble(opt.get("rho"))));

                } else if ("put".equalsIgnoreCase(type)) {

                    putOIList.add(Long.parseLong(opt.get("open_interest").toString()));
                    putStrikeList.add(round2(parseDouble(opt.get("strike"))));
                    putVolumeList.add(Long.parseLong(opt.get("volume").toString()));
                    putIVList.add(round2(parseDouble(opt.get("implied_volatility"))));
                    putDeltaList.add(round2(parseDouble(opt.get("delta"))));
                    putGammaList.add(round2(parseDouble(opt.get("gamma"))));
                    putThetaList.add(round2(parseDouble(opt.get("theta"))));
                    putVegaList.add(round2(parseDouble(opt.get("vega"))));
                    putRhoList.add(round2(parseDouble(opt.get("rho"))));
                }
            }

            OptionDashboard dashboard = repository
                    .findBySymbolAndContractId(symbol, contractId)
                    .orElseGet(OptionDashboard::new);

            dashboard.setSymbol(symbol);
            dashboard.setContractId(contractId);
            dashboard.setDate(LocalDate.parse(expiration));

            dashboard.setCallOpenInterest(callOIList.toArray(new Long[0]));
            dashboard.setCallStrikePrice(callStrikeList.toArray(new Double[0]));
            dashboard.setCallVolume(callVolumeList.toArray(new Long[0]));
            dashboard.setCallImpliedVolatility(callIVList.toArray(new Double[0]));
            dashboard.setCallDelta(callDeltaList.toArray(new Double[0]));
            dashboard.setCallGamma(callGammaList.toArray(new Double[0]));
            dashboard.setCallTheta(callThetaList.toArray(new Double[0]));
            dashboard.setCallVega(callVegaList.toArray(new Double[0]));
            dashboard.setCallRho(callRhoList.toArray(new Double[0]));

            dashboard.setPutOpenInterest(putOIList.toArray(new Long[0]));
            dashboard.setPutStrikePrice(putStrikeList.toArray(new Double[0]));
            dashboard.setPutVolume(putVolumeList.toArray(new Long[0]));
            dashboard.setPutImpliedVolatility(putIVList.toArray(new Double[0]));
            dashboard.setPutDelta(putDeltaList.toArray(new Double[0]));
            dashboard.setPutGamma(putGammaList.toArray(new Double[0]));
            dashboard.setPutTheta(putThetaList.toArray(new Double[0]));
            dashboard.setPutVega(putVegaList.toArray(new Double[0]));
            dashboard.setPutRho(putRhoList.toArray(new Double[0]));

            calculateMetrics(dashboard);

            repository.save(dashboard);
        }
    }

    private void calculateMetrics(OptionDashboard dash) {

        Long[] callOI = dash.getCallOpenInterest();
        Long[] putOI = dash.getPutOpenInterest();
        Long[] callVol = dash.getCallVolume();
        Long[] putVol = dash.getPutVolume();
        Double[] callStrikes = dash.getCallStrikePrice();
        Double[] putStrikes = dash.getPutStrikePrice();

        long totalCallOI = 0;
        long totalPutOI = 0;
        long totalCallVol = 0;
        long totalPutVol = 0;

        double resistance = 0;
        double support = 0;
        long maxCallOI = 0;
        long maxPutOI = 0;

        for (int i = 0; i < callOI.length; i++) {
            totalCallOI += callOI[i];
            totalCallVol += callVol[i];
            if (callOI[i] > maxCallOI) {
                maxCallOI = callOI[i];
                resistance = callStrikes[i];
            }
        }

        for (int i = 0; i < putOI.length; i++) {
            totalPutOI += putOI[i];
            totalPutVol += putVol[i];
            if (putOI[i] > maxPutOI) {
                maxPutOI = putOI[i];
                support = putStrikes[i];
            }
        }

        dash.setResistance(round2(resistance));
        dash.setSupport(round2(support));

        double oiRatio = totalPutOI == 0 ? 0 : (double) totalCallOI / totalPutOI;
        double volRatio = totalPutVol == 0 ? 0 : (double) totalCallVol / totalPutVol;
        double pcr = totalCallOI == 0 ? 0 : (double) totalPutOI / totalCallOI;

        dash.setCallPutOIRatio(round2(oiRatio));
        dash.setCallPutVolumeRatio(round2(volRatio));
        dash.setPcr(round2(pcr));

        if (pcr > 1.2) dash.setBias("BULLISH");
        else if (pcr < 0.8) dash.setBias("BEARISH");
        else dash.setBias("RANGE");

        dash.setMaxPain(round2(calculateMaxPain(dash)));
    }

    private double calculateMaxPain(OptionDashboard dash) {

        Double[] callStrikes = dash.getCallStrikePrice();
        Long[] callOI = dash.getCallOpenInterest();
        Double[] putStrikes = dash.getPutStrikePrice();
        Long[] putOI = dash.getPutOpenInterest();

        Set<Double> allStrikes = new HashSet<>();
        Collections.addAll(allStrikes, callStrikes);
        Collections.addAll(allStrikes, putStrikes);

        double minPain = Double.MAX_VALUE;
        double maxPainStrike = 0;

        for (double s : allStrikes) {

            double totalLoss = 0;

            for (int i = 0; i < callStrikes.length; i++) {
                totalLoss += Math.max(0, (s - callStrikes[i]) * callOI[i]);
            }

            for (int i = 0; i < putStrikes.length; i++) {
                totalLoss += Math.max(0, (putStrikes[i] - s) * putOI[i]);
            }

            if (totalLoss < minPain) {
                minPain = totalLoss;
                maxPainStrike = s;
            }
        }

        return maxPainStrike;
    }

    private double round2(double value) {
        return new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double parseDouble(Object obj) {
        return Double.parseDouble(obj.toString());
    }
}
