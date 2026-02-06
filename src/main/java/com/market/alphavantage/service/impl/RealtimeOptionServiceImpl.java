package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.RealtimeOptionDTO;
import com.market.alphavantage.entity.RealtimeOption;
import com.market.alphavantage.repository.RealtimeOptionRepository;
import com.market.alphavantage.service.RealtimeOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RealtimeOptionServiceImpl implements RealtimeOptionService {

    private final RealtimeOptionRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadRealtimeOptions(String symbol) {

        String url = baseUrl
                + "?function=REALTIME_OPTIONS"
                + "&symbol=" + symbol
                + "&require_greeks=true"
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        List<Map<String, Object>> optionData = (List<Map<String, Object>>) response.get("optionChain");

        if (optionData == null) return;

        RealtimeOption entity = new RealtimeOption();
        entity.setSymbol(symbol);

        List<String> expirationDate = new ArrayList<>();
        List<String> optionType = new ArrayList<>();
        List<Double> strikePrice = new ArrayList<>();
        List<Double> lastPrice = new ArrayList<>();
        List<Double> bid = new ArrayList<>();
        List<Double> ask = new ArrayList<>();
        List<Long> volume = new ArrayList<>();
        List<Long> openInterest = new ArrayList<>();
        List<Double> impliedVolatility = new ArrayList<>();
        List<Double> delta = new ArrayList<>();
        List<Double> gamma = new ArrayList<>();
        List<Double> theta = new ArrayList<>();
        List<Double> vega = new ArrayList<>();

        for (Map<String, Object> opt : optionData) {
            expirationDate.add((String) opt.get("expirationDate"));
            optionType.add((String) opt.get("optionType"));
            strikePrice.add(parseDouble(opt.get("strikePrice")));
            lastPrice.add(parseDouble(opt.get("lastPrice")));
            bid.add(parseDouble(opt.get("bid")));
            ask.add(parseDouble(opt.get("ask")));
            volume.add(parseLong(opt.get("volume")));
            openInterest.add(parseLong(opt.get("openInterest")));
            impliedVolatility.add(parseDouble(opt.get("impliedVolatility")));
            delta.add(parseDouble(opt.get("delta")));
            gamma.add(parseDouble(opt.get("gamma")));
            theta.add(parseDouble(opt.get("theta")));
            vega.add(parseDouble(opt.get("vega")));
        }

        entity.setExpirationDate(expirationDate);
        entity.setOptionType(optionType);
        entity.setStrikePrice(strikePrice);
        entity.setLastPrice(lastPrice);
        entity.setBid(bid);
        entity.setAsk(ask);
        entity.setVolume(volume);
        entity.setOpenInterest(openInterest);
        entity.setImpliedVolatility(impliedVolatility);
        entity.setDelta(delta);
        entity.setGamma(gamma);
        entity.setTheta(theta);
        entity.setVega(vega);

        repository.save(entity);
    }

    @Override
    public RealtimeOptionDTO getRealtimeOptions(String symbol) {
        RealtimeOption e = repository.findById(symbol).orElse(null);
        if (e == null) return null;

        return new RealtimeOptionDTO(
                e.getSymbol(),
                e.getExpirationDate(),
                e.getOptionType(),
                e.getStrikePrice(),
                e.getLastPrice(),
                e.getBid(),
                e.getAsk(),
                e.getVolume(),
                e.getOpenInterest(),
                e.getImpliedVolatility(),
                e.getDelta(),
                e.getGamma(),
                e.getTheta(),
                e.getVega()
        );
    }

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
}
