package com.market.alphavantage.bls;

import com.market.alphavantage.bls.model.Datum;
import com.market.alphavantage.bls.model.Root;
import com.market.alphavantage.entity.CommonData;
import com.market.alphavantage.repository.CommonDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Service
public class BlsDataImpl {

    @Autowired
    CommonDataRepository stockPriceRepository;

    public void blsData(String seriesId, String todayDate) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "https://api.bls.gov/publicAPI/v2/timeseries/data/" + seriesId + "?registrationKey=9bc10307f34d41dbbbe97dad88e53e6a";
        Root response
                = restTemplate.getForObject(fooResourceUrl, Root.class);

        CommonData stockPrice = new CommonData();
        stockPrice.setSymbol(response.getResults().getSeries().get(0).seriesID);
        ArrayList<Datum> data = response.getResults().getSeries().get(0).getData();
        Double[] close = new Double[data.size() - 1];
        String[] date = new String[data.size() - 1];
        for (int i = 0; i < data.size() - 1; i++) {

            Datum datum = data.get(i);
            date[i] = datum.getYear() + datum.getPeriod();
            if (!datum.getValue().equals("-")) {
                 close[i] =  Double.parseDouble(datum.getValue());
            }
        }
        System.out.println(seriesId + "Done");


        stockPrice.setClose(reverse(close, close.length));
        stockPrice.setDates(reverseDate(date, date.length));
       stockPrice.setType("economic");
        stockPrice.setSource("bls");
        stockPriceRepository.save(stockPrice);

    }

    public Double[] reverse(Double a[], int n) {
        Double[] b = new Double[n];
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }
        // printing the reversed array
        System.out.println("Reversed array is: \n");
        return b;
    }

    public String[] reverseDate(String a[], int n) {
        String[] b = new String[n];
        int j = n;
        for (int i = 0; i < n; i++) {
            b[j - 1] = a[i];
            j = j - 1;
        }
        // printing the reversed array
        System.out.println("Reversed array is: \n");
        return b;
    }



    public static void main(String[] args) throws IOException {

    }


}
