package polygon.shortinterest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import polygon.shortinterest.model.Result;
import polygon.shortinterest.model.Root;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ShortInterest {
    //@Autowired
   // AnalyticsRepository analyticsRepository;

    public void shortTickersList(String url) {
         try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("shortTickersList Start");
        RestTemplate restTemplate = new RestTemplate();
        String response = "https://api.massive.com/stocks/v1/short-interest?limit=10&sort=ticker.asc&settlement_date.gte="+today+"&apiKey=qTzWblajukAnwLG2C6HEr3yOQFxaAizw";
        if (!url.isEmpty()) {
            response = url + "&apiKey=qTzWblajukAnwLG2C6HEr3yOQFxaAizw";
        }
        Root root
                = restTemplate.getForObject(response, Root.class);
        List<Result> tickersList = root.getResults();
        saveResults(tickersList);
        if (root.getNext_url() != null && !root.getNext_url().isEmpty()) {
            shortTickersList(root.getNext_url());
        }
        System.out.println("activeTickersList Done");
    }

    public void saveResults(List<Result> results) {
      /*  List<ActiveTickers> dbList = new ArrayList<>();
        for (Result result : results) {
            Double perc = 0.0;
            int vol = 0;
            if (result != null) {

                perc = BigDecimal.valueOf(result.getAvg_daily_volume())
                        .divide(BigDecimal.valueOf(result.getShort_interest()), 2, RoundingMode.HALF_UP)
                        .doubleValue();

                vol = result.getShort_interest();
                try {
                    analyticsRepository.updateShortData(perc, vol, result.getTicker());
                } catch (Exception ex) {
                    System.out.println("Update Short List" + ex);
                }

            }*/

        }
    }

