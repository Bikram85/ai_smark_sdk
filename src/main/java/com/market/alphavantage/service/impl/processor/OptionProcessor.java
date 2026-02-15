package com.market.alphavantage.service.impl.processor;

import com.market.alphavantage.entity.RealtimeOption;
import com.market.alphavantage.repository.RealtimeOptionRepository;
import com.market.alphavantage.repository.SymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OptionProcessor {
    private final RealtimeOptionRepository repository;

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
                + "&symbol=" + symbol.toUpperCase()
                + "&require_greeks=true"
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        List<Map<String, Object>> optionData =
                (List<Map<String, Object>>) response.get("data");

        if (optionData == null || optionData.isEmpty()) return;

        /* ---- Split calls & puts ---- */

        List<Map<String, Object>> calls = new ArrayList<>();
        List<Map<String, Object>> puts = new ArrayList<>();

        for (Map<String, Object> opt : optionData) {
            String type = (String) opt.get("type");

            if ("call".equalsIgnoreCase(type)) {
                calls.add(opt);
            } else if ("put".equalsIgnoreCase(type)) {
                puts.add(opt);
            }
        }

        RealtimeOption entity = new RealtimeOption();
        entity.setSymbol(symbol.toUpperCase());

        /* ---- Fill CALL arrays ---- */

        fillOptionArrays(calls, true, entity);

        /* ---- Fill PUT arrays ---- */

        fillOptionArrays(puts, false, entity);

        repository.save(entity);

        logInfo("Saved Realtime Options for "
                + symbol + " Calls=" + calls.size()
                + " Puts=" + puts.size());
    }

    private void fillOptionArrays(List<Map<String, Object>> data,
                                  boolean isCall,
                                  RealtimeOption entity) {

        int size = data.size();

        String[] expiration = new String[size];
        Double[] strike = new Double[size];
        Double[] last = new Double[size];
        Double[] bid = new Double[size];
        Double[] ask = new Double[size];
        Long[] volume = new Long[size];
        Long[] openInterest = new Long[size];
        Double[] iv = new Double[size];
        Double[] delta = new Double[size];
        Double[] gamma = new Double[size];
        Double[] theta = new Double[size];
        Double[] vega = new Double[size];

        for (int i = 0; i < size; i++) {

            Map<String, Object> opt = data.get(i);

            expiration[i] = (String) opt.get("expiration");
            strike[i] = parseDouble(opt.get("strike"));
            last[i] = parseDouble(opt.get("last"));
            bid[i] = parseDouble(opt.get("bid"));
            ask[i] = parseDouble(opt.get("ask"));
            volume[i] = parseLong(opt.get("volume"));
            openInterest[i] = parseLong(opt.get("open_interest"));
            iv[i] = parseDouble(opt.get("impliedVolatility"));
            delta[i] = parseDouble(opt.get("delta"));
            gamma[i] = parseDouble(opt.get("gamma"));
            theta[i] = parseDouble(opt.get("theta"));
            vega[i] = parseDouble(opt.get("vega"));
        }

        if (isCall) {
            entity.setCallExpirationDate(expiration);
            entity.setCallStrikePrice(strike);
            entity.setCallLastPrice(last);
            entity.setCallBid(bid);
            entity.setCallAsk(ask);
            entity.setCallVolume(volume);
            entity.setCallOpenInterest(openInterest);
            entity.setCallImpliedVolatility(iv);
            entity.setCallDelta(delta);
            entity.setCallGamma(gamma);
            entity.setCallTheta(theta);
            entity.setCallVega(vega);
        } else {
            entity.setPutExpirationDate(expiration);
            entity.setPutStrikePrice(strike);
            entity.setPutLastPrice(last);
            entity.setPutBid(bid);
            entity.setPutAsk(ask);
            entity.setPutVolume(volume);
            entity.setPutOpenInterest(openInterest);
            entity.setPutImpliedVolatility(iv);
            entity.setPutDelta(delta);
            entity.setPutGamma(gamma);
            entity.setPutTheta(theta);
            entity.setPutVega(vega);
        }
    }

    /* ===== Helpers ===== */

    private Double parseDouble(Object val) {
        try {
            return val == null ? 0.0 : Double.valueOf(val.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Long parseLong(Object val) {
        try {
            return val == null ? 0L : Long.valueOf(val.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    private void logInfo(String message) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] INFO: " + message);
    }

    private void logError(String message) {
        System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + message);
    }


}
