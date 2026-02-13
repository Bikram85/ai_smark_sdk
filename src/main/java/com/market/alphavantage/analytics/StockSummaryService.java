package com.market.alphavantage.analytics;

import com.market.alphavantage.dto.StockSummaryDTO;
import com.market.alphavantage.entity.*;
import com.market.alphavantage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockSummaryService {

    private final BalanceSheetRepository balanceSheetRepository;
    private final CashFlowRepository cashFlowRepository;
    private final IncomeStatementRepository incomeStatementRepository;
    private final StockPriceRepository stockPriceRepository;
    private final EquityTechnicalIndicatorRepository technicalRepository;

    public StockSummaryDTO getStockSummary(String symbol) {
        StockSummaryDTO dto = new StockSummaryDTO();

        // ===== Revenue =====
        IncomeStatement is = incomeStatementRepository.findById(symbol).orElse(null);
        if (is != null) {
            StockSummaryDTO.RevenueDTO rev = new StockSummaryDTO.RevenueDTO();
            rev.labels = Arrays.asList(is.getAnnualFiscalDateEnding());
            rev.totalRevenue = Arrays.asList(is.getAnnualTotalRevenue());
            rev.annualCostOfRevenue = Arrays.asList(is.getAnnualCostOfRevenue());
            rev.grossProfit = Arrays.asList(is.getAnnualGrossProfit());
            rev.operatingExpense = Arrays.asList(is.getAnnualOperatingExpenses());
            rev.operatingIncome = Arrays.asList(is.getAnnualOperatingIncome());
            rev.incomeBeforeTax = Arrays.asList(is.getAnnualIncomeBeforeTax());
            rev.netIncome = Arrays.asList(is.getAnnualNetIncome());
            dto.revenue = rev;
        }

        // ===== Cash Flow =====
        CashFlow cf = cashFlowRepository.findById(symbol).orElse(null);
        if (cf != null) {
            StockSummaryDTO.CashFlowDTO cash = new StockSummaryDTO.CashFlowDTO();
            cash.labels = Arrays.asList(cf.getAnnualFiscalDateEnding());
            cash.finance = Arrays.asList(cf.getAnnualCashflowFromFinancing());
            cash.investment = Arrays.asList(cf.getAnnualCashflowFromInvestment());
            cash.operating = Arrays.asList(cf.getAnnualOperatingCashflow());
            cash.changeInCash = Arrays.asList(cf.getAnnualChangeInCash());
            dto.cashFlow = cash;
        }

        // ===== Balance Sheet =====
        BalanceSheet bs = balanceSheetRepository.findById(symbol).orElse(null);
        if (bs != null) {
            StockSummaryDTO.BalanceSheetDTO balance = new StockSummaryDTO.BalanceSheetDTO();
            balance.labels = Arrays.asList(bs.getAnnualFiscalDateEnding());
            balance.totalAssets = Arrays.asList(bs.getAnnualTotalAssets());
            balance.liabilities = Arrays.asList(bs.getAnnualTotalLiabilities());
            balance.equity = Arrays.asList(bs.getAnnualTotalShareholderEquity());
            dto.balanceSheet = balance;
        }

        // ===== Stock Price & Volume =====
        StockPrice sp = stockPriceRepository.findById(symbol).orElse(null);
        if (sp != null) {
            StockSummaryDTO.PriceDTO price = new StockSummaryDTO.PriceDTO();
            price.labels = Arrays.asList(sp.getTradeDates());
            price.close = Arrays.asList(sp.getClose());
            price.volume = Arrays.asList(sp.getVolume());
            dto.price = price;
        }

        // ===== Technical Indicators =====
      /*  List<EquityTechnicalIndicator> indicators = technicalRepository.findAllBySymbol(symbol);
        if (indicators != null && !indicators.isEmpty()) {
            StockSummaryDTO.IndicatorsDTO ind = new StockSummaryDTO.IndicatorsDTO();

            // Assuming SMA, RSI, MACD each have one entry
            indicators.forEach(i -> {
                if (i.getFunction().equalsIgnoreCase("SMA")) {
                    ind.sma = Arrays.asList(i.getValues());
                    ind.labels = Arrays.asList(i.getDates());
                } else if (i.getFunction().equalsIgnoreCase("RSI")) {
                    ind.rsi = Arrays.asList(i.getValues());
                } else if (i.getFunction().equalsIgnoreCase("MACD")) {
                    ind.macd = Arrays.asList(i.getValues());
                }
            });
            dto.indicators = ind;
        }*/

        return dto;
    }
}
