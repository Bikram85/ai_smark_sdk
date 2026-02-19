package com.market.alphavantage.yahoo.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.market.alphavantage.entity.IndexPrice;
import com.market.alphavantage.repository.IndexPriceRepository;
import lombok.RequiredArgsConstructor;
import org.brotli.dec.BrotliInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.market.alphavantage.yahoo.model.chart.Quote;
import com.market.alphavantage.yahoo.model.chart.Result;
import com.market.alphavantage.yahoo.model.chart.Root;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockPriceImpl {

    @Autowired
    private final IndexPriceRepository repository;


    public void captureTickerPriceFromYahoo(String symbol)  {
        try {
            try {
                TimeUnit.SECONDS.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Start Capture Yahoo Stock Price   " + symbol);
            String todayDate = new Date().toString();
            // 10 years ago
            Calendar from = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            from.add(Calendar.YEAR, -10);
            long tenYearsAgo = from.getTimeInMillis() / 1000L;

            String encodedSymbol = URLEncoder.encode(symbol, StandardCharsets.UTF_8);

            // Now
            Calendar to = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            long current = to.getTimeInMillis() / 1000L;

            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + encodedSymbol +
                    "?period1=" + tenYearsAgo +
                    "&period2=" + current +
                    "&interval=1d" +
                    "&includeAdjustedClose=true" +
                    "&includePrePost=true" +
                    "&events=div%7Csplit%7Cearn" +
                    "&lang=en-US" +
                    "&region=US";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/124.0.0.0")
                    .header("Accept-Encoding", "gzip, br")
                    .header("Accept", "application/json, text/plain, */*")
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            byte[] bodyBytes = response.body();
            String json;

            // Check Content-Encoding
            String encoding = response.headers().firstValue("Content-Encoding").orElse("");
            if (encoding.equalsIgnoreCase("br")) {
                // Brotli decode
                BrotliInputStream bis = new BrotliInputStream(new ByteArrayInputStream(bodyBytes));
                json = new String(bis.readAllBytes());
            } else if (encoding.equalsIgnoreCase("gzip")) {
                // GZIP handled automatically if using BodyHandlers.ofString(), but we used byte[] so decode manually
                java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(new ByteArrayInputStream(bodyBytes));
                json = new String(gis.readAllBytes());
            } else {
                json = new String(bodyBytes);
            }

            // Parse JSON into POJO
            ObjectMapper mapper = new ObjectMapper();
            Root root = mapper.readValue(json, Root.class);

            if (root != null && root.getChart() != null && root.getChart().getResult() != null) {
                for (Result result : root.getChart().getResult()) {

                    List<String> dates = new ArrayList<>();
                    if (result.getTimestamp() != null) {
                        for (Long ts : result.getTimestamp()) {
                            dates.add(new SimpleDateFormat("yyyy-MM-dd").format(ts * 1000L));
                        }
                    }

                    if (result.getIndicators() != null && result.getIndicators().getQuote() != null) {
                        for (Quote quote : result.getIndicators().getQuote()) {
                            IndexPrice entity = IndexPrice.builder()
                                    .symbol(symbol)
                                    .country("")
                                    .name("")
                                    .dates(dates.toArray(new String[0]))
                                    .open(quote.getHigh().toArray(new Double[quote.getHigh().size()]))
                                    .high(quote.getHigh().toArray(new Double[quote.getHigh().size()]))
                                    .low(quote.getLow().toArray(new Double[quote.getLow().size()]))
                                    .close(quote.getClose().toArray(new Double[quote.getClose().size()]))
                                    .volume(toDoubleArray(quote.getVolume()))
                                    .build();

                            repository.save(entity);

                        }
                    }
                }
            } else {
                System.out.println("No data returned for symbol: " + symbol);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Ticker Proce from Yahoo " + ex);
        }

        System.out.println("End Capture Yahoo Stock Price   " + symbol);
    }

    private Long[] toDoubleArray(List<?> list) {
        if (list == null) return null;

        Long[] result = new Long[list.size()];

        for (int i = 0; i < list.size(); i++) {
            Object val = list.get(i);

            if (val == null) {
                result[i] = null;
            } else if (val instanceof Number) {
                result[i] = ((Number) val).longValue();  // <— SAFE CONVERSION
            } else {
                throw new IllegalArgumentException(
                        "Cannot convert value to Double: " + val);
            }
        }

        return result;
    }
}

