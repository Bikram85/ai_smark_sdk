package com.market.alphavantage.scheduler;

import com.market.alphavantage.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class DataScheduler {

    private final MarketService marketService;
    private final CompanyOverviewService companyOverviewService;
    private final BalanceSheetService balanceSheetService;
    private final CashFlowService cashFlowService;
    private final DividendService dividendService;
    private final CommodityService commodityService;
    private final DigitalCurrencyDailyService digitalCurrencyDailyService;
    private final EarningsCalendarService earningsCalendarService;
    private final EquityTechnicalIndicatorService equityTechnicalIndicatorService;
    private final ForexTechnicalIndicatorService forexTechnicalIndicatorService;
    private final FxDailyService fxDailyService;
    private final GoldSilverHistoryService goldSilverHistoryService;
    private final IncomeStatementService incomeStatementService;
    private final InsiderTransactionService insiderTransactionService;
    private final IpoCalendarService ipoCalendarService;
    private final RealtimeOptionService realtimeOptionService;
    private final SharesOutstandingService sharesOutstandingService;
    private final TopGainersLosersService topGainersLosersService;

    @Scheduled(cron = "0 54 18 * * FRI")
    public void initDataSet() throws ParseException, IOException {
       // marketService.loadListingStatus();
        marketService.loadDailyPrices();
       // companyOverviewService.loadOverview();
       // balanceSheetService.loadBalanceSheet();
        cashFlowService.loadCashFlow();
        incomeStatementService.loadIncomeStatement();
        dividendService.loadDividends();
        insiderTransactionService.loadInsiderTransactions();

        sharesOutstandingService.loadSharesOutstanding();
        equityTechnicalIndicatorService.loadSMA();

        topGainersLosersService.loadTopGainersLosers();


        realtimeOptionService.loadRealtimeOptions();

        fxDailyService.loadFxDaily();
        //forexTechnicalIndicatorService.loadSMA();

        goldSilverHistoryService.loadHistory();
        commodityService.loadCommodity();


        digitalCurrencyDailyService.loadDigitalCurrencyDaily();


        earningsCalendarService.loadEarningsCalendar("3month");
        ipoCalendarService.loadIpoCalendar();


    }
}
