package com.market.alphavantage.analytics;

import com.market.alphavantage.dto.OptionDashboardDTO;
import com.market.alphavantage.entity.RealtimeOption;
import com.market.alphavantage.repository.RealtimeOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionAnalyticsService {

    private final RealtimeOptionRepository repository;

    public List<OptionDashboardDTO> analyzeAll() {

        List<RealtimeOption> all = repository.findAll();

        List<OptionDashboardDTO> result = new ArrayList<>();

        for (RealtimeOption data : all) {
            result.add(analyzeSingle(data));
        }

        return result;
    }

    private OptionDashboardDTO analyzeSingle(RealtimeOption data) {

        long totalCallOI = 0;
        long totalPutOI = 0;

        long maxCallOI = 0;
        long maxPutOI = 0;

        double resistance = 0;
        double support = 0;

        Double[] callStrikes = data.getCallStrikePrice();
        Long[] callOI = data.getCallOpenInterest();

        Double[] putStrikes = data.getPutStrikePrice();
        Long[] putOI = data.getPutOpenInterest();

        for (int i = 0; i < callOI.length; i++) {
            if (callOI[i] == null) continue;

            totalCallOI += callOI[i];

            if (callOI[i] > maxCallOI) {
                maxCallOI = callOI[i];
                resistance = callStrikes[i];
            }
        }

        for (int i = 0; i < putOI.length; i++) {
            if (putOI[i] == null) continue;

            totalPutOI += putOI[i];

            if (putOI[i] > maxPutOI) {
                maxPutOI = putOI[i];
                support = putStrikes[i];
            }
        }

        double pcr = totalCallOI == 0
                ? 0
                : (double) totalPutOI / totalCallOI;

        String bias;
        if (pcr > 1.2) bias = "BULLISH";
        else if (pcr < 0.8) bias = "BEARISH";
        else bias = "RANGE";

        return new OptionDashboardDTO(
                data.getSymbol(),
                support,
                resistance,
                totalCallOI,
                totalPutOI,
                pcr,
                bias
        );

    }
}

