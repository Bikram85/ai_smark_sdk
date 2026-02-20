package com.market.alphavantage.service.impl;

import com.market.alphavantage.dto.CompanyOverviewDTO;
import com.market.alphavantage.entity.CompanyOverview;
import com.market.alphavantage.entity.Symbol;
import com.market.alphavantage.repository.CompanyOverviewRepository;
import com.market.alphavantage.repository.SymbolRepository;
import com.market.alphavantage.service.CompanyOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CompanyOverviewServiceImpl implements CompanyOverviewService {

    private final RestTemplate restTemplate;
    private final CompanyOverviewRepository repo;
    private final SymbolRepository symbolRepo;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void loadOverview() {
        List<Symbol> stocks = symbolRepo.findByAssetType("Stock");
       // List<Symbol> etfs = symbolRepo.findByAssetType("ETF");

        int total = stocks.size();
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        stocks.forEach(symbol -> processSymbol(symbol.getSymbol(), processed, success, failed, total));
       // etfs.forEach(symbol -> processSymbol(symbol.getSymbol(), processed, success, failed, total));

        logInfo("\n===== SUMMARY =====");
        logInfo("Total loadOverview symbols : " + total);
        logInfo("Success       : " + success.get());
        logInfo("Failed        : " + failed.get());
    }

    private void processSymbol(String symbol,
                               AtomicInteger processed,
                               AtomicInteger success,
                               AtomicInteger failed,
                               int total) {

        int current = processed.incrementAndGet();

        try {
            fetchDetails(symbol);
            success.incrementAndGet();
            logInfo("Processed loadOverview " + current + "/" + total + " SUCCESS: " + symbol);

        } catch (Exception ex) {
            failed.incrementAndGet();
            logInfo("Processed loadOverview " + current + "/" + total + " FAILED: " + symbol + " Reason: " + ex.getMessage());
        }
    }

    private void fetchDetails(String symbol) {

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = baseUrl + "?function=OVERVIEW&symbol=" + symbol + "&apikey=" + apiKey;
        Map<String, Object> res = restTemplate.getForObject(url, Map.class);
        if (res == null || res.isEmpty()) {
            logInfo("No response for symbol: " + symbol);
            return;
        }

        String sym = (String) res.get("Symbol");
        if (sym == null || sym.isBlank()) {
            logInfo("Skipping symbol because Symbol field is null or empty: " + symbol);
            return; // must have ID set
        }

        CompanyOverview c = new CompanyOverview();
        c.setSymbol(sym); // mandatory
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

        try {
            repo.save(c);
        } catch (Exception e) {
            logInfo("Failed to save company: " + sym + " Reason: " + e.getMessage());
        }
    }

    @Override
    public CompanyOverviewDTO getOverview(String symbol) {
        return repo.findById(symbol)
                .map(c -> new CompanyOverviewDTO(
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
                ))
                .orElse(null);
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

    private void logInfo(String msg) {
        System.out.println("[" + dtf.format(LocalDateTime.now()) + "] " + msg);
    }
}
