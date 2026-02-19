package com.market.alphavantage.fred;

import com.market.alphavantage.entity.CommonData;
import com.market.alphavantage.fred.model.Root;
import com.market.alphavantage.repository.CommonDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FredDataImpl {


    @Autowired
    CommonDataRepository macroRepository;

    public void FredData(String seriesId, String todayDate) throws IOException {
        System.out.println(seriesId);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "https://api.stlouisfed.org/fred/series/observations?series_id="+seriesId+"&api_key=1d800744c0340b11386599c5c0fdd887&observation_start=1776-07-04&observation_end=9999-12-31&file_type=json";
        Root response
                = restTemplate.getForObject(fooResourceUrl, Root.class);

        CommonData stockPrice = new CommonData();
       if(response != null && response.getObservations() != null)
        stockPrice.setSymbol(seriesId);
       List<Double> close = new ArrayList<>();
       List<String> date = new ArrayList<>();
        // Double[] close = new Double[response.getObservations().size() - 1];
       // String[] date = new String[response.getObservations().size() - 1];
        int j = 0;
        for(int i = 0; i < response.getObservations().size()  ; i++)
        {
            try {
                if(response.getObservations().get(i).getDate() != null) {
                    close.add(Double.valueOf(response.getObservations().get(i).getValue()));
                    date.add(response.getObservations().get(i).getDate());
                    j++;
                }
            }
            catch (Exception ex) {

               // System.out.println("Exception Occur" + ex);
            }

        }

         Double[] lastclose = new Double[close.size() - 1];
         String[] lastdate = new String[date.size() - 1];
         lastclose =  close.toArray(new Double[close.size() -1]);
         lastdate = date.toArray(new String[date.size()-1]);
        System.out.println(seriesId + "Done");
       stockPrice.setClose(lastclose);

        stockPrice.setType("fred");
        stockPrice.setSource("fred");
        macroRepository.save(stockPrice);

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
        FredDataImpl microEco = new FredDataImpl();
        //microEco.captureFredData();
    }


}
