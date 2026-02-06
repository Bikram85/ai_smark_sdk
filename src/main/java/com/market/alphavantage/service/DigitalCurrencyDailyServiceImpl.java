package com.market.alphavantage.service;

import com.market.alphavantage.dto.DigitalCurrencyDailyDTO;
import com.market.alphavantage.entity.DigitalCurrencyDaily;
import com.market.alphavantage.repository.DigitalCurrencyDailyRepository;
import com.market.alphavantage.service.DigitalCurrencyDailyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DigitalCurrencyDailyServiceImpl implements DigitalCurrencyDailyService {

    private final DigitalCurrencyDailyRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadDigitalCurrencyDaily(String symbol, String market) {
        String url = baseUrl
                + "?function=DIGITAL_CURRENCY_DAILY"
                + "&symbol=" + symbol.toUpperCase()
                + "&market=" + market.toUpperCase()
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        Map<String, Map<String, String>> series =
                (Map<String, Map<String, String>>) response.get("Time Series (Digital Currency Daily)");
        if (series == null) return;

        DigitalCurrencyDaily entity = new DigitalCurrencyDaily();
        String id = symbol.toUpperCase() + "_" + market.toUpperCase();
        entity.setId(id);
        entity.setSymbol(symbol.toUpperCase());
        entity.setMarket(market.toUpperCase());

        List<LocalDate> tradeDate = new ArrayList<>();
        List<Double> open = new ArrayList<>();
        List<Double> high = new ArrayList<>();
        List<Double> low = new ArrayList<>();
        List<Double> close = new ArrayList<>();
        List<Double> volume = new ArrayList<>();
        List<Double> marketCap = new ArrayList<>();

        series.forEach((date, values) -> {
            tradeDate.add(parseDate(date));
            open.add(parseDouble(values.get("1a. open (" + market.toUpperCase() + ")")));
            high.add(parseDouble(values.get("2a. high (" + market.toUpperCase() + ")")));
            low.add(parseDouble(values.get("3a. low (" + market.toUpperCase() + ")")));
            close.add(parseDouble(values.get("4a. close (" + market.toUpperCase() + ")")));
            volume.add(parseDouble(values.get("5. volume")));
            marketCap.add(parseDouble(values.get("6. market cap (" + market.toUpperCase() + ")")));
        });

        entity.setTradeDate(tradeDate);
        entity.setOpen(open);
        entity.setHigh(high);
        entity.setLow(low);
        entity.setClose(close);
        entity.setVolume(volume);
        entity.setMarketCap(marketCap);

        repository.save(entity);
    }

    @Override
    public DigitalCurrencyDailyDTO getDigitalCurrencyDaily(String symbol, String market) {
        String id = symbol.toUpperCase() + "_" + market.toUpperCase();
        DigitalCurrencyDaily e = repository.findById(id).orElse(null);
        if (e == null) return null;

        return new DigitalCurrencyDailyDTO(
                e.getId(),
                e.getSymbol(),
                e.getMarket(),
                e.getTradeDate(),
                e.getOpen(),
                e.getHigh(),
                e.getLow(),
                e.getClose(),
                e.getVolume(),
                e.getMarketCap()
        );
    }

    private LocalDate parseDate(String val) {
        try {
            return val == null || val.isBlank() ? null : LocalDate.parse(val);
        } catch (Exception ex) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return val == null || val.isBlank() ? 0.0 : Double.valueOf(val);
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
