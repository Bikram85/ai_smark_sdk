package com.market.alphavantage.service;

import com.market.alphavantage.entity.IncomeStatement;
import com.market.alphavantage.repository.IncomeStatementRepository;
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
public class IncomeStatementServiceImpl implements IncomeStatementService {

    private final IncomeStatementRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Override
    public void loadIncomeStatement(String symbol) {

        String url = baseUrl +
                "?function=INCOME_STATEMENT" +
                "&symbol=" + symbol +
                "&apikey=" + apiKey;

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("annualReports"))
            return;

        List<Map<String, String>> annualReports =
                (List<Map<String, String>>) response.get("annualReports");

        List<Map<String, String>> quarterlyReports =
                (List<Map<String, String>>) response.get("quarterlyReports");

        IncomeStatement entity = new IncomeStatement();
        entity.setSymbol(symbol);

        /* ---------- Annual ---------- */

        List<LocalDate> annualDates = new ArrayList<>();
        List<Long> annualRevenue = new ArrayList<>();
        List<Long> annualGrossProfit = new ArrayList<>();
        List<Long> annualOperatingIncome = new ArrayList<>();
        List<Long> annualNetIncome = new ArrayList<>();
        List<Long> annualEbitda = new ArrayList<>();

        for (Map<String, String> r : annualReports) {
            annualDates.add(parseDate(r.get("fiscalDateEnding")));
            annualRevenue.add(parseLong(r.get("totalRevenue")));
            annualGrossProfit.add(parseLong(r.get("grossProfit")));
            annualOperatingIncome.add(parseLong(r.get("operatingIncome")));
            annualNetIncome.add(parseLong(r.get("netIncome")));
            annualEbitda.add(parseLong(r.get("ebitda")));
        }

        entity.setAnnualFiscalDateEnding(annualDates);
        entity.setAnnualTotalRevenue(annualRevenue);
        entity.setAnnualGrossProfit(annualGrossProfit);
        entity.setAnnualOperatingIncome(annualOperatingIncome);
        entity.setAnnualNetIncome(annualNetIncome);
        entity.setAnnualEbitda(annualEbitda);

        /* ---------- Quarterly ---------- */

        List<LocalDate> qDates = new ArrayList<>();
        List<Long> qRevenue = new ArrayList<>();
        List<Long> qGrossProfit = new ArrayList<>();
        List<Long> qOperatingIncome = new ArrayList<>();
        List<Long> qNetIncome = new ArrayList<>();
        List<Long> qEbitda = new ArrayList<>();

        for (Map<String, String> r : quarterlyReports) {
            qDates.add(parseDate(r.get("fiscalDateEnding")));
            qRevenue.add(parseLong(r.get("totalRevenue")));
            qGrossProfit.add(parseLong(r.get("grossProfit")));
            qOperatingIncome.add(parseLong(r.get("operatingIncome")));
            qNetIncome.add(parseLong(r.get("netIncome")));
            qEbitda.add(parseLong(r.get("ebitda")));
        }

        entity.setQuarterlyFiscalDateEnding(qDates);
        entity.setQuarterlyTotalRevenue(qRevenue);
        entity.setQuarterlyGrossProfit(qGrossProfit);
        entity.setQuarterlyOperatingIncome(qOperatingIncome);
        entity.setQuarterlyNetIncome(qNetIncome);
        entity.setQuarterlyEbitda(qEbitda);

        repository.save(entity);
    }

    @Override
    public IncomeStatement getIncomeStatement(String symbol) {
        return repository.findById(symbol).orElse(null);
    }

    /* ---------- Helpers ---------- */

    private Long parseLong(String value) {
        if (value == null || value.equals("None"))
            return 0L;
        return Long.parseLong(value);
    }

    private LocalDate parseDate(String value) {
        if (value == null)
            return null;
        return LocalDate.parse(value);
    }
}

