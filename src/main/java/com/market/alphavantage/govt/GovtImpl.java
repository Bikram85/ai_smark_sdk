package com.market.alphavantage.govt;

import com.market.alphavantage.entity.CommonData;
import com.market.alphavantage.govt.model.Root;
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
public class GovtImpl {


    @Autowired
    CommonDataRepository macroRepository;

    public void GovtData(String seriesId, String todayDate,String url) throws IOException {
        System.out.println(seriesId);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RestTemplate restTemplate = new RestTemplate();
       Root response
                = restTemplate.getForObject(url, Root.class);

       CommonData stockPrice = null;
       if(seriesId.equalsIgnoreCase("TotalDebt")) {
           stockPrice = debt(seriesId, response, todayDate);
       }
        if(seriesId.equalsIgnoreCase("RevSpenSurDef")) {
            stockPrice = revSpenSurpDef(seriesId, response, todayDate);
        }
        if(stockPrice != null) {
            macroRepository.save(stockPrice);
        }

    }

    public CommonData debt(String seriesId ,Root response,String todayDate)
    {
        CommonData stockPrice = new CommonData();
        if(response != null && response.getData() != null)
            stockPrice.setSymbol(seriesId);
        List<Double> close = new ArrayList<>();
        List<Double> high = new ArrayList<>();
        List<String> date = new ArrayList<>();
        // Double[] close = new Double[response.getObservations().size() - 1];
        // String[] date = new String[response.getObservations().size() - 1];
        int j = 0;
        for(int i = 0; i < response.getData().size() -1 ; i++)
        {
            try {
                if(response.getData().get(i).getTot_pub_debt_out_amt()!= null ) {
                    close.add(Double.valueOf(response.getData().get(i).getIntragov_hold_amt()));
                    high.add(Double.valueOf(response.getData().get(i).getDebt_held_public_amt()));
                  //  date.add(response.getData().get(i).getRecord_calendar_month()+"/"+response.getData().get(i).getRecord_calendar_day()+"/"+response.getData().get(i).getRecord_calendar_year());
                    date.add(response.getData().get(i).getRecord_date());
                    j++;
                }
            }
            catch (Exception ex) {

                // System.out.println("Exception Occur" + ex);
            }

        }
        Double[]  lastclose =  close.toArray(new Double[close.size() -1]);
        Double[] lasthigh =  high.toArray(new Double[high.size() -1]);
        String[] lastdate = date.toArray(new String[date.size()-1]);
        System.out.println(seriesId + "Done");
        stockPrice.setClose(lastclose);
        stockPrice.setHigh(lasthigh);
        stockPrice.setType("Govt");
        stockPrice.setSource("Govt");

        return  stockPrice;
    }

    public CommonData revSpenSurpDef(String seriesId ,Root response,String todayDate)
    {
        CommonData stockPrice = new CommonData();
        if(response != null && response.getData() != null)
            stockPrice.setSymbol(seriesId);
        List<Double> close = new ArrayList<>();
        List<Double> high = new ArrayList<>();
        List<Double> low = new ArrayList<>();
        List<String> date = new ArrayList<>();
        // Double[] close = new Double[response.getObservations().size() - 1];
        // String[] date = new String[response.getObservations().size() - 1];
        int j = 0;
        for(int i = 0; i < response.getData().size() -1 ; i++)
        {
            try {
                if(response.getData().get(i).getCurrent_month_budget_amt()!= null ) {
                    if(response.getData().get(i).getClassification_desc()!= null && response.getData().get(i).getClassification_desc().equalsIgnoreCase("Total Receipts")) {
                        close.add(Double.valueOf(response.getData().get(i).getCurrent_month_budget_amt()));
                    }
                    if(response.getData().get(i).getClassification_desc()!= null && response.getData().get(i).getClassification_desc().equalsIgnoreCase("Total Outlays")) {
                        high.add(Double.valueOf(response.getData().get(i).getCurrent_month_budget_amt()));
                    }
                    if(response.getData().get(i).getClassification_desc()!= null && response.getData().get(i).getClassification_desc().contains("Total Surplus")) {
                        low.add(Double.valueOf(response.getData().get(i).getCurrent_month_budget_amt()));
                    }
                    if(!date.contains(response.getData().get(i).getRecord_date()))
                    {
                        date.add(response.getData().get(i).getRecord_date());
                    }
                    j++;
                }
            }
            catch (Exception ex) {

                // System.out.println("Exception Occur" + ex);
            }

        }
        Double[]  lastclose =  close.toArray(new Double[close.size() -1]);
        Double[] lasthigh =  high.toArray(new Double[high.size() -1]);
        Double[] lastlow =  low.toArray(new Double[low.size() -1]);
        String[] lastdate = date.toArray(new String[date.size()-1]);
        System.out.println(seriesId + "Done");
        stockPrice.setClose(lastclose);
        stockPrice.setHigh(lasthigh);
        stockPrice.setLow(lastlow);
        stockPrice.setType("Govt");
        stockPrice.setSource("Govt");
        return  stockPrice;
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
        System.out.println(System.getProperty("javax.net.ssl.trustStore"));
        GovtImpl microEco = new GovtImpl();
        // microEco.captureGovtData();
    }


}

