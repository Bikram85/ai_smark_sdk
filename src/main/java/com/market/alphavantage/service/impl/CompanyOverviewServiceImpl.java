package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.CompanyOverviewDTO;
import com.market.alphavantage.entity.CompanyOverview;
import com.market.alphavantage.repository.CompanyOverviewRepository;
import com.market.alphavantage.service.CompanyOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyOverviewServiceImpl implements CompanyOverviewService {

    private final RestTemplate restTemplate;
    private final CompanyOverviewRepository repo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadOverview(String symbol) {

        String url = baseUrl
                + "?function=OVERVIEW"
                + "&symbol=" + symbol
                + "&apikey=" + apiKey;

        Map<String, Object> res =
                restTemplate.getForObject(url, Map.class);

        if (res == null || res.isEmpty()) return;

        CompanyOverview c = new CompanyOverview();

        c.setSymbol((String) res.get("Symbol"));
        c.setAssetType((String) res.get("AssetType"));
        c.setName((String) res.get("Name"));
        c.setDescription((String) res.get("Description"));
        c.setCik((String) res.get("CIK"));
        c.setExchange((String) res.get("Exchange"));
        c.setCurrency((String) res.get("Currency"));
        c.setCountry((String) res.get("Country"));
        c.setSector((String) res.get("Sector"));
        c.setIndustry((String) res.get("Industry"));
        c.setAddress((String) res.get("Address"));
        c.setOfficialSite((String) res.get("OfficialSite"));
        c.setFiscalYearEnd((String) res.get("FiscalYearEnd"));

        c.setLatestQuarter(parseDate(res.get("LatestQuarter")));

        c.setMarketCapitalization(parseLong(res.get("MarketCapitalization")));
        c.setEbitda(parseLong(res.get("EBITDA")));

        c.setPeRatio(parseDouble(res.get("PERatio")));
        c.setPegRatio(parseDouble(res.get("PEGRatio")));
        c.setBookValue(parseDouble(res.get("BookValue")));

        c.setDividendPerShare(parseDouble(res.get("DividendPerShare")));
        c.setDividendYield(parseDouble(res.get("DividendYield")));

        c.setEps(parseDouble(res.get("EPS")));
        c.setRevenuePerShareTTM(parseDouble(res.get("RevenuePerShareTTM")));
        c.setProfitMargin(parseDouble(res.get("ProfitMargin")));
        c.setOperatingMarginTTM(parseDouble(res.get("OperatingMarginTTM")));
        c.setReturnOnAssetsTTM(parseDouble(res.get("ReturnOnAssetsTTM")));
        c.setReturnOnEquityTTM(parseDouble(res.get("ReturnOnEquityTTM")));

        c.setRevenueTTM(parseLong(res.get("RevenueTTM")));
        c.setGrossProfitTTM(parseLong(res.get("GrossProfitTTM")));

        c.setDilutedEPSTTM(parseDouble(res.get("DilutedEPSTTM")));

        c.setQuarterlyEarningsGrowthYOY(parseDouble(res.get("QuarterlyEarningsGrowthYOY")));
        c.setQuarterlyRevenueGrowthYOY(parseDouble(res.get("QuarterlyRevenueGrowthYOY")));

        c.setAnalystTargetPrice(parseDouble(res.get("AnalystTargetPrice")));

        c.setAnalystRatingStrongBuy(parseInt(res.get("AnalystRatingStrongBuy")));
        c.setAnalystRatingBuy(parseInt(res.get("AnalystRatingBuy")));
        c.setAnalystRatingHold(parseInt(res.get("AnalystRatingHold")));
        c.setAnalystRatingSell(parseInt(res.get("AnalystRatingSell")));
        c.setAnalystRatingStrongSell(parseInt(res.get("AnalystRatingStrongSell")));

        c.setTrailingPE(parseDouble(res.get("TrailingPE")));
        c.setForwardPE(parseDouble(res.get("ForwardPE")));
        c.setPriceToSalesRatioTTM(parseDouble(res.get("PriceToSalesRatioTTM")));
        c.setPriceToBookRatio(parseDouble(res.get("PriceToBookRatio")));
        c.setEvToRevenue(parseDouble(res.get("EVToRevenue")));
        c.setEvToEBITDA(parseDouble(res.get("EVToEBITDA")));

        c.setBeta(parseDouble(res.get("Beta")));

        c.setWeek52High(parseDouble(res.get("52WeekHigh")));
        c.setWeek52Low(parseDouble(res.get("52WeekLow")));

        c.setMovingAvg50Day(parseDouble(res.get("50DayMovingAverage")));
        c.setMovingAvg200Day(parseDouble(res.get("200DayMovingAverage")));

        c.setSharesOutstanding(parseLong(res.get("SharesOutstanding")));
        c.setSharesFloat(parseLong(res.get("SharesFloat")));

        c.setPercentInsiders(parseDouble(res.get("PercentInsiders")));
        c.setPercentInstitutions(parseDouble(res.get("PercentInstitutions")));

        c.setDividendDate(parseDate(res.get("DividendDate")));
        c.setExDividendDate(parseDate(res.get("ExDividendDate")));

        repo.save(c);
    }

    @Override
    public CompanyOverviewDTO getOverview(String symbol) {

        CompanyOverview c = repo.findById(symbol).orElse(null);
        if (c == null) return null;

        return new CompanyOverviewDTO(
                c.getSymbol(),
                c.getAssetType(),
                c.getName(),
                c.getDescription(),
                c.getExchange(),
                c.getCurrency(),
                c.getCountry(),
                c.getSector(),
                c.getIndustry(),
                c.getMarketCapitalization(),
                c.getPeRatio(),
                c.getPegRatio(),
                c.getEps(),
                c.getDividendYield(),
                c.getAnalystTargetPrice(),
                c.getWeek52High(),
                c.getWeek52Low(),
                c.getSharesOutstanding()
        );
    }

    private Double parseDouble(Object v) {
        try {
            if (v == null) return null;
            String s = v.toString();
            if (s.isBlank() || s.equals("None") || s.equals("-")) return null;
            return Double.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(Object v) {
        try {
            if (v == null) return null;
            String s = v.toString();
            if (s.isBlank() || s.equals("None") || s.equals("-")) return null;
            return Long.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(Object v) {
        try {
            if (v == null) return null;
            return Integer.valueOf(v.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDate(Object v) {
        try {
            if (v == null) return null;
            return LocalDate.parse(v.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

