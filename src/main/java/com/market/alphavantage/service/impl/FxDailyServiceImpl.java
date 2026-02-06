package com.market.alphavantage.service.impl;



import com.market.alphavantage.dto.FxDailyDTO;
import com.market.alphavantage.entity.FxDaily;
import com.market.alphavantage.repository.FxDailyRepository;
import com.market.alphavantage.service.FxDailyService;
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
public class FxDailyServiceImpl implements FxDailyService {

    private final FxDailyRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;


    @Override
    public void loadFxDaily() {
        fetchDetails("JPY","USD");
        fetchDetails("AED","USD");
        fetchDetails("CAD","USD");
        fetchDetails("CHF","USD");
        fetchDetails("EUR","USD");
        fetchDetails("GBP","USD");
        fetchDetails("INR","USD");
        fetchDetails("RUB","USD");
        fetchDetails("SAR","USD");

    }


    private void fetchDetails(String fromSymbol, String toSymbol) {
        String url = baseUrl
                + "?function=FX_DAILY"
                + "&from_symbol=" + fromSymbol.toUpperCase()
                + "&to_symbol=" + toSymbol.toUpperCase()
                + "&outputsize=full"
                + "&apikey=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.isEmpty()) return;

        Map<String, Map<String, String>> series =
                (Map<String, Map<String, String>>) response.get("Time Series FX (Daily)");
        if (series == null) return;

        FxDaily entity = new FxDaily();
        String id = fromSymbol.toUpperCase() + "_" + toSymbol.toUpperCase();
        entity.setId(id);
        entity.setFromSymbol(fromSymbol.toUpperCase());
        entity.setToSymbol(toSymbol.toUpperCase());

        List<LocalDate> tradeDates = new ArrayList<>();
        List<Double> opens = new ArrayList<>();
        List<Double> highs = new ArrayList<>();
        List<Double> lows = new ArrayList<>();
        List<Double> closes = new ArrayList<>();

        series.forEach((date, values) -> {
            tradeDates.add(parseDate(date));
            opens.add(parseDouble(values.get("1. open")));
            highs.add(parseDouble(values.get("2. high")));
            lows.add(parseDouble(values.get("3. low")));
            closes.add(parseDouble(values.get("4. close")));
        });

        entity.setTradeDate(tradeDates);
        entity.setOpen(opens);
        entity.setHigh(highs);
        entity.setLow(lows);
        entity.setClose(closes);

        repository.save(entity);
    }

    @Override
    public FxDailyDTO getFxDaily(String fromSymbol, String toSymbol) {
        String id = fromSymbol.toUpperCase() + "_" + toSymbol.toUpperCase();
        FxDaily e = repository.findById(id).orElse(null);
        if (e == null) return null;

        return new FxDailyDTO(
                e.getId(),
                e.getFromSymbol(),
                e.getToSymbol(),
                e.getTradeDate(),
                e.getOpen(),
                e.getHigh(),
                e.getLow(),
                e.getClose()
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
