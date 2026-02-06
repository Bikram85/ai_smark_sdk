package com.market.alphavantage.service;



import com.market.alphavantage.dto.InsiderTransactionDTO;
import com.market.alphavantage.entity.InsiderTransaction;
import com.market.alphavantage.repository.InsiderTransactionRepository;
import com.market.alphavantage.service.InsiderTransactionService;
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
public class InsiderTransactionServiceImpl implements InsiderTransactionService {

    private final InsiderTransactionRepository repository;
    private final RestTemplate restTemplate;

    @Value("${alphavantage.baseUrl}")
    private String baseUrl;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    @Override
    public void loadInsiderTransactions(String symbol) {

        String url = baseUrl
                + "?function=INSIDER_TRANSACTIONS"
                + "&symbol=" + symbol
                + "&apikey=" + apiKey;

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        if (response == null || response.isEmpty())
            return;

        // Some APIs wrap the actual list in a key like "insiderTransactions"
        List<Map<String, String>> list =
                (List<Map<String, String>>) response.get("insiderTransactions");

        if (list == null)
            return;

        InsiderTransaction e = new InsiderTransaction();
        e.setSymbol(symbol);

        List<LocalDate> dates = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> relationships = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> ownerships = new ArrayList<>();
        List<Long> transacted = new ArrayList<>();
        List<Long> owned = new ArrayList<>();
        List<Double> avgPrices = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        for (Map<String, String> r : list) {
            dates.add(parseDate(r.get("transactionDate")));
            names.add(r.get("insiderName"));
            relationships.add(r.get("relationship"));
            types.add(r.get("transactionType"));
            ownerships.add(r.get("ownershipType"));
            transacted.add(parseLong(r.get("sharesTransacted")));
            owned.add(parseLong(r.get("sharesOwned")));
            avgPrices.add(parseDouble(r.get("averagePrice")));
            titles.add(r.get("reportedTitle"));
        }

        e.setTransactionDate(dates);
        e.setInsiderName(names);
        e.setRelationship(relationships);
        e.setTransactionType(types);
        e.setOwnershipType(ownerships);
        e.setSharesTransacted(transacted);
        e.setSharesOwned(owned);
        e.setAvgPrice(avgPrices);
        e.setReportedTitle(titles);

        repository.save(e);
    }

    @Override
    public InsiderTransactionDTO getInsiderTransactions(String symbol) {
        InsiderTransaction e = repository.findById(symbol).orElse(null);
        if (e == null) return null;

        return new InsiderTransactionDTO(
                e.getSymbol(),
                e.getTransactionDate(),
                e.getInsiderName(),
                e.getRelationship(),
                e.getTransactionType(),
                e.getOwnershipType(),
                e.getSharesTransacted(),
                e.getSharesOwned(),
                e.getAvgPrice(),
                e.getReportedTitle()
        );
    }

    /* ===== Helpers ===== */
    private LocalDate parseDate(String val) {
        try {
            return (val == null || val.isBlank()) ? null : LocalDate.parse(val);
        } catch (Exception ex) {
            return null;
        }
    }

    private Long parseLong(String val) {
        try {
            return (val == null || val.isBlank()) ? 0L : Long.valueOf(val);
        } catch (Exception ex) {
            return 0L;
        }
    }

    private Double parseDouble(String val) {
        try {
            return (val == null || val.isBlank()) ? 0.0 : Double.valueOf(val);
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
