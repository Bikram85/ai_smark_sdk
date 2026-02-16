package com.market.alphavantage.analytics;

import com.market.alphavantage.entity.OptionDashboard;
import com.market.alphavantage.entity.RealtimeOption;
import com.market.alphavantage.repository.OptionDashboardRepository;
import com.market.alphavantage.repository.RealtimeOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionAnalyticsService {

    private final RealtimeOptionRepository realtimeRepo;
    private final OptionDashboardRepository dashboardRepo;

    /**
     * Analyze all options and save results in DB
     */
    @Transactional
    public void analyzeAndSaveAll() {
        List<RealtimeOption> allOptions = realtimeRepo.findAll();
        for (RealtimeOption option : allOptions) {
            analyzeAndSaveSingle(option);
        }
    }

    /**
     * Analyze a single option and save/update in DB
     */
    private void analyzeAndSaveSingle(RealtimeOption data) {

        if (data == null) return;

        long totalCallOI = 0L;
        long totalPutOI = 0L;
        long maxCallOI = 0L;
        long maxPutOI = 0L;
        double resistance = 0.0;
        double support = 0.0;

        Double[] callStrikes = data.getCallStrikePrice();
        Long[] callOI = data.getCallOpenInterest();
        Double[] putStrikes = data.getPutStrikePrice();
        Long[] putOI = data.getPutOpenInterest();

        // Safety checks
        if (callStrikes == null || callOI == null || putStrikes == null || putOI == null) return;

        // Analyze Call options
        for (int i = 0; i < callOI.length; i++) {
            if (callOI[i] == null) continue;

            totalCallOI += callOI[i];
            if (callOI[i] > maxCallOI) {
                maxCallOI = callOI[i];
                resistance = callStrikes[i] != null ? callStrikes[i] : resistance;
            }
        }

        // Analyze Put options
        for (int i = 0; i < putOI.length; i++) {
            if (putOI[i] == null) continue;

            totalPutOI += putOI[i];
            if (putOI[i] > maxPutOI) {
                maxPutOI = putOI[i];
                support = putStrikes[i] != null ? putStrikes[i] : support;
            }
        }

        double pcr = totalCallOI == 0 ? 0.0 : (double) totalPutOI / totalCallOI;

        String bias;
        if (pcr > 1.2) bias = "BULLISH";
        else if (pcr < 0.8) bias = "BEARISH";
        else bias = "RANGE";

        // Save or update OptionDashboard in DB
        OptionDashboard dashboard = dashboardRepo.findBySymbol(data.getSymbol())
                .orElseGet(() -> {
                    OptionDashboard newDashboard = new OptionDashboard();
                    newDashboard.setSymbol(data.getSymbol());
                    return newDashboard;
                });

        dashboard.setSupport(support);
        dashboard.setResistance(resistance);
       // dashboard.setTotalCallOI(totalCallOI);
       // dashboard.setTotalPutOI(totalPutOI);
        dashboard.setPcr(pcr);
        dashboard.setBias(bias);

        dashboardRepo.save(dashboard);
    }
}
