package com.market.alphavantage.service.impl;



import com.market.alphavantage.dto.SharesOutstandingDTO;
import com.market.alphavantage.entity.SharesOutstanding;
import com.market.alphavantage.repository.SharesOutstandingRepository;

import com.market.alphavantage.service.SharesOutstandingService;
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
public class SharesOutstandingServiceImpl implements SharesOutstandingService {

    private final SharesOutstandingRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadSharesOutstanding(String symbol) {

        String url = baseUrl
                + "?function=SHARES_OUTSTANDING"
                + "&symbol=" + symbol
                + "&apikey=" + apiKey;

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("annualReports")) return;

        List<Map<String, String>> reports =
                (List<Map<String, String>>) response.get("annualReports");

        SharesOutstanding entity = new SharesOutstanding();
        entity.setSymbol(symbol);

        List<LocalDate> dates = new ArrayList<>();
        List<Long> sharesList = new ArrayList<>();

        for (Map<String, String> r : reports) {
            dates.add(parseDate(r.get("fiscalDateEnding")));
            sharesList.add(parseLong(r.get("reportedSharesOutstanding")));
        }

        entity.setFiscalDateEnding(dates);
        entity.setReportedSharesOutstanding(sharesList);

        repository.save(entity);
    }

    @Override
    public SharesOutstandingDTO getSharesOutstanding(String symbol) {
        SharesOutstanding e = repository.findById(symbol).orElse(null);
        if (e == null) return null;

        return new SharesOutstandingDTO(
                e.getSymbol(),
                e.getFiscalDateEnding(),
                e.getReportedSharesOutstanding()
        );
    }

    /* ===== Helpers ===== */
    private Long parseLong(String val) {
        if (val == null || val.isBlank()) return 0L;
        try { return Long.valueOf(val); }
        catch (Exception ignored) { return 0L; }
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.isBlank()) return null;
        return LocalDate.parse(val);
    }
}

