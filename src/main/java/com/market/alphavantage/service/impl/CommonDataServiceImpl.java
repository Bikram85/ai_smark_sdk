package com.market.alphavantage.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.alphavantage.bls.BlsDataImpl;
import com.market.alphavantage.dto.CommonDataDTO;
import com.market.alphavantage.dto.IndexPriceDTO;
import com.market.alphavantage.entity.CommonData;
import com.market.alphavantage.entity.IndexPrice;
import com.market.alphavantage.fred.FredDataImpl;
import com.market.alphavantage.govt.GovtImpl;
import com.market.alphavantage.repository.CommonDataRepository;
import com.market.alphavantage.repository.IndexPriceRepository;
import com.market.alphavantage.util.IndexConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.market.alphavantage.yahoo.impl.StockPriceImpl;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommonDataServiceImpl {

    private final CommonDataRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    StockPriceImpl stockPrice;

    @Autowired
    BlsDataImpl blsData;

    @Autowired
    GovtImpl govt;

    @Autowired
    FredDataImpl fredData;

    @Value("${alphavantage.apiKey}")
    private String apiKey;

    /**
     * Fetches all popular indices listed in IndexConstants and saves in DB.
     */
    public void fetchAllPopularIndices() {
        captureIndex();
        try {
            captureBlsData();
            captureGovtData();
            captureFredData();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    /**
     * Fetches index data from Alpha Vantage API and saves to DB.
     */
    public void fetchAndSave(IndexConstants idx) {
        try {
            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED"
                    + "&symbol=" + idx.getSymbol()
                    + "&outputsize=full"
                    + "&apikey=" + apiKey;

            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = mapper.readTree(response);
            JsonNode series = root.get("Time Series (Daily)");

            if (series == null)
                throw new RuntimeException("Invalid API response for " + idx.getSymbol());

            List<String> dates = new ArrayList<>();
            List<Double> open = new ArrayList<>();
            List<Double> high = new ArrayList<>();
            List<Double> low = new ArrayList<>();
            List<Double> close = new ArrayList<>();
            List<Long> volume = new ArrayList<>();

            Iterator<String> fields = series.fieldNames();
            while (fields.hasNext()) {
                String date = fields.next();
                JsonNode day = series.get(date);

                dates.add(date);
                open.add(day.get("1. open").asDouble());
                high.add(day.get("2. high").asDouble());
                low.add(day.get("3. low").asDouble());
                close.add(day.get("4. close").asDouble());
                volume.add(day.get("6. volume").asLong()); // Adjusted API key for volume
            }

            // Ensure oldest first
            Collections.reverse(dates);
            Collections.reverse(open);
            Collections.reverse(high);
            Collections.reverse(low);
            Collections.reverse(close);
            Collections.reverse(volume);

            CommonData entity = CommonData.builder()
                    .symbol(idx.getSymbol())
                    .country(idx.getCountry())
                    .name(idx.getName())
                    .type("index")
                    .source("Yahoo")
                    .dates(dates.toArray(new String[0]))
                    .open(open.toArray(new Double[0]))
                    .high(high.toArray(new Double[0]))
                    .low(low.toArray(new Double[0]))
                    .close(close.toArray(new Double[0]))
                    .volume(volume.toArray(new Long[0]))
                    .build();

            repository.save(entity);



        } catch (Exception e) {
            throw new RuntimeException("Failed fetching index data for " + idx.getSymbol(), e);
        }
    }

    /**
     * Get saved index from DB.
     */
    public CommonDataDTO getFromDB(String symbol) {
        CommonData entity = repository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Index not found: " + symbol));
        return toDTO(entity);
    }

    /**
     * Get all saved indices from DB
     */
    public List<CommonDataDTO> getAllIndices() {
        List<CommonData> entities = repository.findAll();
        List<CommonDataDTO> result = new ArrayList<>();
        for (CommonData e : entities) {
            result.add(toDTO(e));
        }
        return result;
    }

    private CommonDataDTO toDTO(CommonData e) {
        return CommonDataDTO.builder()
                .symbol(e.getSymbol())
                .name(e.getName())
                .country(e.getCountry())
                .dates(Arrays.asList(e.getDates()))
                .open(Arrays.asList(e.getOpen()))
                .high(Arrays.asList(e.getHigh()))
                .low(Arrays.asList(e.getLow()))
                .close(Arrays.asList(e.getClose()))
                .volume(Arrays.asList(e.getVolume()))
                .build();
    }

    public void captureIndex(){

        //  100 biggest companies by market capitalisation on the London Stock Exchange (LSE).
        stockPrice.captureTickerPriceFromYahoo("^FTSE");
        //  Index also gives a general idea of the direction of the Euronext Paris,
        //  the largest stock exchange in France formerly known as the Paris Bourse..
        stockPrice.captureTickerPriceFromYahoo("^FCHI");
        //Xetra is a fully electronic trading platform. Headquartered in Frankfurt, Germany,
        // the exchange is operated by Deutsche Börse Group, which also owns the Frankfurt Stock Exchange (FRA) or Frankfurter Wertpapierbörse
        stockPrice.captureTickerPriceFromYahoo("^GDAXI");
        //Swiss Market Index
        stockPrice.captureTickerPriceFromYahoo("^SSMI");
        //stock index consisting of the largest and most liquid companies on the Italian national stock exchange
        stockPrice.captureTickerPriceFromYahoo("FTSEMIB.MI");
        //Spanish stock exchange
        stockPrice.captureTickerPriceFromYahoo("^IBEX");

        //Dow Jones Industrial Average (^DJI)
        stockPrice.captureTickerPriceFromYahoo("^DJI");
        //Russell 2000 (^RUT)
        stockPrice.captureTickerPriceFromYahoo("^RUT");
        // FTSE 100 (^FTSE)
        stockPrice.captureTickerPriceFromYahoo("^FTSE");
        //Chines 500
        stockPrice.captureTickerPriceFromYahoo("GXC");
        //india
        stockPrice.captureTickerPriceFromYahoo("^CRSLDX");
        //SPY
        stockPrice.captureTickerPriceFromYahoo("^GSPC");
        //Nikke
        stockPrice.captureTickerPriceFromYahoo("^N225");
        //Nasdaq Compisite index
        stockPrice.captureTickerPriceFromYahoo("^IXIC");
        //Nasdaq Compisite index
        stockPrice.captureTickerPriceFromYahoo("^VIX");

    }

    public void captureBlsData() throws IOException {
        String todayDate = new Date().toString();
        //Unemployment Rate (Seasonally Adjusted) - LNS14000000
        blsData.blsData("LNS14000000", todayDate);

        // Imports - All Commodities - EIUIR
        blsData.blsData("EIUIR", todayDate);

        // Exports - All Commodities - EIUIQ
        blsData.blsData("EIUIQ", todayDate);

        //CPI CUUR0000SA0
        blsData.blsData("CUUR0000SA0", todayDate);

        //1d800744c0340b11386599c5c0fdd887
        //https://api.stlouisfed.org/fred/series/observations?series_id=SP500&file_type=json&api_key=1d800744c0340b11386599c5c0fdd887&file_type=json
        //CPI CUSR0000SA0 - seasonally adjusted
        blsData.blsData("CUSR0000SA0", todayDate);
    }

    public void captureGovtData() throws IOException {
        String todayDate = new Date().toString();
        String debtUrl = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v2/accounting/od/debt_to_penny?sort=record_date&format=json&page[number]=1&page[size]=10000";
        String revoutdefsurp = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/mts/mts_table_2?sort=record_date&format=json&page[number]=1&page[size]=10000";
        //Unemployment Rate (Seasonally Adjusted) - LNS14000000
        govt.GovtData("TotalDebt", todayDate,debtUrl);
        govt.GovtData("RevSpenSurDef", todayDate,revoutdefsurp);



    }

    public void captureFredData() throws IOException {

        String todayDate = new Date().toString();
        fredData.FredData("GDP", todayDate);

        //NBER based Recession Indicators for the United States from the Period following the Peak through the Trough (USREC)
        fredData.FredData("USREC", todayDate);
        //Unemployment Rate (Seasonally Adjusted) - LNS14000000
        fredData.FredData("SP500", todayDate);
        //Nasadaq composite index
        fredData.FredData("NASDAQCOM",todayDate);
        //Japan Nikkie
        fredData.FredData("NIKKEI225",todayDate);

        //Yen to US
        fredData.FredData("DEXJPUS",todayDate);
        //Yaun to US
        fredData.FredData("DEXCHUS",todayDate);
        //USD to EURO
        fredData.FredData("DEXUSEU", todayDate);

        //Real GDP For Japan
        fredData.FredData("JPNRGDPEXP",todayDate);
        //Real GDP China
        fredData.FredData("NGDPRXDCCNA",todayDate);


        //Japan GDP
        fredData.FredData("JPNNGDP",todayDate);
        //china GDP
        fredData.FredData("MKTGDPCNA646NWDB",todayDate);
        //US GDP
        fredData.FredData("GDP", todayDate);



        //Interest Rate Japan
        fredData.FredData("IRLTLT01JPM156N",todayDate);
        fredData.FredData("INTDSRCNM193N",todayDate); //china


        //bank of japan Total Assets
        fredData.FredData("JPNASSETS",todayDate);
        fredData.FredData("TRESEGCNM052N",todayDate);//China total Reserve -

        //Japan Consumer Price index
        fredData.FredData("JPNCPIALLMINMEI",todayDate);
        fredData.FredData("DDOE01CNA086NWDB",todayDate);//China
        fredData.FredData("CPIAUCSL",todayDate);// USA CPIAUCSL Consumer Price Index for All Urban Consumers: All Items in U.S. City Average



        fredData.FredData("MYAGM0CNM189N",todayDate); //M0 for china  - MYAGM0CNM189N
        fredData.FredData("MYAGM1CNM189N",todayDate); //M1 for china  - MYAGM1CNM189N
        fredData.FredData("MYAGM2CNM189N",todayDate);//M2 for China  - MYAGM2CNM189N
        fredData.FredData("M2SL",todayDate);//M2 for USA
        fredData.FredData("M1SL",todayDate);//M1 for USA


        //Japan Interest Rates
        fredData.FredData("INTDSRJPM193N",todayDate);

        //Japan Debt % to GDP
        fredData.FredData("DEBTTLJPA188A",todayDate);
        //Fed Debt To GDP
        fredData.FredData("GFDEGDQ188S", todayDate);
        fredData.FredData("GGGDTACNA188N", todayDate);//china  - GGGDTACNA188N


        //bank of japan Total Assets
        fredData.FredData("JPNASSETS",todayDate);

        //Fed Sur or Deficit
        fredData.FredData("MTSDS133FMS", todayDate);








        // 30 yr Mortgage Rate USA
        fredData.FredData("MORTGAGE30US", todayDate);

        // Federal Fund rate
        fredData.FredData("DFF", todayDate);

        //Unemployment United States
        fredData.FredData("UNRATE", todayDate); // USA




        //10 yr securitgy
        fredData.FredData("DGS10", todayDate);

        //Inflation rate
        fredData.FredData("T10YIEM", todayDate);



        //Crude Price WTI
        fredData.FredData("DCOILWTICO", todayDate);

        //brent Oil
        fredData.FredData("DCOILBRENTEU", todayDate);



        //US Population
        fredData.FredData("POPTHM",todayDate);
        //Copper
        fredData.FredData("PCOPPUSDM",todayDate);

        //Balance Sheet: Total Assets (QBPBSTAS)
        fredData.FredData("QBPBSTAS", todayDate);


        //Fed current expenditure
        fredData.FredData("FGEXPND",todayDate);
        //Fed Total Public Debt
        fredData.FredData("GFDEBTN", todayDate);
        //Federal Surplus or Deficit [-] (FYFSD)
        fredData.FredData("FYFSD", todayDate);
        //Federal government budget surplus or deficit (-) (M318501Q027NBEA)
        fredData.FredData("M318501Q027NBEA", todayDate);

        //Liabilities and Capital: Liabilities: Deposits with F.R. Banks, Other Than Reserve Balances: U.S. Treasury, General Account: Week Average (WTREGEN)
        fredData.FredData("WTREGEN", todayDate);
        //Assets: Total Assets: Total Assets (Less Eliminations from Consolidation): Wednesday Level (WALCL)
        fredData.FredData("WALCL", todayDate);
        //Assets: Securities Held Outright: U.S. Treasury Securities: All: Wednesday Level (TREAST)
        fredData.FredData("TREAST", todayDate);
        //Assets: Total Assets: Total Assets: Wednesday Level (RESPPANWW)
        fredData.FredData("RESPPANWW", todayDate);

        //Gross Domestic Product (A191RP1Q027SBEA)
        fredData.FredData("A191RP1Q027SBEA", todayDate);
        //Federal Surplus or Deficit [-] (FYFSD)
        fredData.FredData("FYFSD", todayDate);
        //Federal government budget surplus or deficit (-) (M318501Q027NBEA)
        fredData.FredData("M318501Q027NBEA", todayDate);
        //Federal government current tax receipts (W006RC1Q027SBEA)
        fredData.FredData("W006RC1Q027SBEA", todayDate);
        //Federal Debt Held by Federal Reserve Banks (FDHBFRBN)
        fredData.FredData("FDHBFRBN", todayDate);

        //Monetary Base: Total (BOGMBASE)
        fredData.FredData("BOGMBASE", todayDate);
        //Federal government current expenditures: Interest payments (A091RC1Q027SBEA)
        fredData.FredData("A091RC1Q027SBEA", todayDate);
        //Overnight Reverse Repurchase Agreements Award Rate: Treasury Securities Sold by the Federal Reserve in the Temporary Open Market Operations (RRPONTSYAWARD)
        fredData.FredData("RRPONTSYAWARD", todayDate);
        //Liabilities and Capital: Other Factors Draining Reserve Balances: Reserve Balances with Federal Reserve Banks: Week Average (WRESBAL)
        fredData.FredData("A091RC1Q027SBEA", todayDate);
        //Currency in Circulation (CURRCIR)
        fredData.FredData("CURRCIR", todayDate);
        //Government total expenditures (W068RCQ027SBEA)
        fredData.FredData("W068RCQ027SBEA", todayDate);
        //Gross Federal Debt (FYGFD)
        fredData.FredData("FYGFD", todayDate);
        //Federal Debt Held by the Public (FYGFDPUN)
        fredData.FredData("FYGFDPUN", todayDate);


        //Deposits, All Commercial Banks (DPSACBW027SBOG)
        fredData.FredData("DPSACBW027SBOG", todayDate);
        //Housing Inventory: Active Listing Count in the United States (ACTLISCOUUS)
        fredData.FredData("ACTLISCOUUS", todayDate);



        //CBOE Volatility Index: VIX (VIXCLS)
        fredData.FredData("VIXCLS", todayDate);



        //Imports of Goods and Services (IMPGS)
        fredData.FredData("IMPGS", todayDate);
        //Trade Balance: Goods and Services, Balance of Payments Basis (BOPGSTB)
        fredData.FredData("BOPGSTB", todayDate);
        //Net Exports of Goods and Services (NETEXP)
        fredData.FredData("NETEXP", todayDate);

        //Global price of Aluminum (PALUMUSDM)
        fredData.FredData("PALUMUSDM", todayDate);

        //NASDAQ 100 Index (NASDAQ100)
        fredData.FredData("NASDAQ100", todayDate);













        //China US import Goods  - IMPCH
        //U.S. Imports of Goods by Customs Basis from China

        //central bank asset to GDp  - China  - DDDI06CNA156NWDB



        //Consumer Price Index: All Items: Total for China CHNCPALTT01IXNBM
        //Index source base, Monthly, Not Seasonally Adjusted

        //Real Residential Property Prices for China - QCNR628BIS


    }

}
