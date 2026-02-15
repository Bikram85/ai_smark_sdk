package com.market.alphavantage.analytics;

import com.market.alphavantage.entity.*;
import com.market.alphavantage.repository.*;
import com.market.alphavantage.service.impl.processor.AnalyticsProcessor;
import com.market.alphavantage.util.AnalyticsMath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsAlgoService {

    private final SymbolRepository symbolRepo;

    @Autowired
    AnalyticsProcessor analyticsProcessor;


    /* ================= BATCH ================= */
    @Transactional
    public void runBatchAnalytics() {
        List<Symbol> symbols = symbolRepo.findByAssetType("Stock");
int siz = symbols.size();
int count = 0;
        for (Symbol sym : symbols) {
            try {
                System.out.println(sym + " Processing " + "Total Size"  + " " + siz  + "Current Processing" + count++);
                analyticsProcessor.processSymbol(sym);
            } catch (Exception e) {
                log.error("Analytics failed {}", sym.getSymbol(), e);
            }
        }
    }


}
