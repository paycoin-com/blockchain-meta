package io.hs.bex.currency.handler;


import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.model.stats.CoinInfo;
import io.hs.bex.currency.service.api.CurrencyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;


//-------------------------------------

@JsonIgnoreProperties( ignoreUnknown = true )
class InfoResponse
{
    public InfoResponse()
    {
    }

    @JsonProperty( "Response" )
    public String response;

    @JsonProperty( "Message" )
    public String message;

    @JsonProperty( "Type" )
    public int type = 0;

    @JsonProperty( "Aggregated" )
    public boolean aggregated = false;

    @JsonProperty( "Data" )
    public List<Data> dataList;
}

@JsonIgnoreProperties( ignoreUnknown = true )
class Data
{
    public Data()
    {
    }

    @JsonProperty( "time" )
    public long time;

    @JsonProperty( "close" )
    public double close = 0;

    @JsonProperty( "high" )
    public double high = 0;

    @JsonProperty( "low" )
    public double low = 0;

    @JsonProperty( "open" )
    public double open = 0;
}

@JsonIgnoreProperties( ignoreUnknown = true )
class CCCoinInfoRaw
{
    @JsonProperty( "RAW" )
    public Map<String, Map<String, CCCoinInfo>> coins;

}

@JsonIgnoreProperties( ignoreUnknown = true )
class CCCoinInfo
{
    public CCCoinInfo()
    {
    }

    @JsonProperty( "SUPPLY" )
    public long supply;

    @JsonProperty( "PRICE" )
    public double price = 0;

    @JsonProperty( "VOLUME24HOUR" )
    public double volume23h = 0;
}

@Service( "CryptoCompareHandler" )
public class CryptoCompareHandler implements CurrencyInfoService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CryptoCompareHandler.class );
    // ---------------------------------

    @Value( "${service.ccy.cryptocompare.api.url}" )
    private String infoServiceUrl;

    @Autowired
    private ObjectMapper mapper;

    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @PostConstruct
    public void init()
    {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.add( "user-agent", "Mozilla/5.0" );
    }

    @Override
    public List<CurrencyRate> getLatestXRates(CurrencyInfoRequest request)
    {
        String url = infoServiceUrl + "/data/pricemulti?relaxedValidation=true&fsyms="
                + request.joinSourceSecondaryCcys( "," ) + "&tsyms=" + request.joinTargetCurrencies( "," );

        if ( !Strings.isNullOrEmpty( request.getXStockSource() ) )
            url += "&e=" + request.getXStockSource();

        try
        {
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            return jsonToCurrencyRates( response.getBody() );
        }
        catch ( Exception e )
        {
            logger.error( "Error getting Currency rate from:{}", url, e );

            return null;
        }
    }

    @Override
    public List<CoinInfo> getCoinInfo(CurrencyInfoRequest request)
    {
        String url = infoServiceUrl + "/data/pricemultifull?relaxedValidation=true&fsyms="
                + request.joinSourceSecondaryCcys( "," ) + "&tsyms=" + request.joinTargetCurrencies( "," );

        if ( !Strings.isNullOrEmpty( request.getXStockSource() ) )
            url += "&e=" + request.getXStockSource();

        try
        {
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            return jsonToCoinInfoList( response.getBody() );
        }
        catch ( Exception e )
        {
            logger.error( "Error getting Currency rate from:{}", url, e );

            return null;
        }
    }

    @Override
    public CurrencyRate getXRate(String sourceCurrency, String targetCurrency)
    {
        String url = infoServiceUrl + "/data/price?fsym=" + sourceCurrency.toUpperCase() + "&tsyms="
                + sourceCurrency.toUpperCase();

        try
        {
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            return jsonToCurrencyRate( sourceCurrency, targetCurrency, response.getBody() );
        }
        catch ( Exception e )
        {
            logger.error( "Error getting Currency rate from:{}", url, e );

            return null;
        }
    }

    @Override
    public List<CurrencyRate> getXRatesBy(CurrencyInfoRequest request)
    {
        String url = constructUrl( request );

        try
        {
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            return jsonToCurrencyRateList( request.getSourceCurrencies().get( 0 ),
                    request.getTargetCurrencies().get( 0 ), response.getBody() );
        }
        catch ( Exception e )
        {
            logger.error( "Error getting Currency rate list from:{}", url, e );

            return Collections.emptyList();
        }
    }

    private String constructUrl(CurrencyInfoRequest request)
    {
        String url = "";

        if ( request.getPeriod() == TimePeriod.YEAR )
        {

        }
        else if ( request.getPeriod() == TimePeriod.MONTH )
        {

        }
        else if ( request.getPeriod() == TimePeriod.DAY )
        {
            url = infoServiceUrl + "/data/histoday?aggregate=1&fsym=" + request.getSourceSecondaryCode() + "&tsym="
                    + request.getTargetCcyCode() + "&limit=" + request.getLimit() + "&toTs="
                    + request.getDateTo().getEpochSecond();
        }
        else if ( request.getPeriod() == TimePeriod.HOUR )
        {
            url = infoServiceUrl + "/data/histohour?aggregate=1&fsym=" + request.getSourceSecondaryCode() + "&tsym="
                    + request.getTargetCcyCode() + "&limit=" + request.getLimit() + "&toTs="
                    + request.getDateTo().getEpochSecond();
        }
        else if ( request.getPeriod() == TimePeriod.MINUTE )
        {
            url = infoServiceUrl + "/data/histominute?aggregate=1&fsym=" + request.getSourceSecondaryCode() + "&tsym="
                    + request.getTargetCcyCode() + "&limit=" + request.getLimit() + "&toTs="
                    + request.getDateTo().getEpochSecond();
        }

        if ( !Strings.isNullOrEmpty( request.getXStockSource() ) )
            url += "&e=" + request.getXStockSource();

        return url;
    }

    private CurrencyRate jsonToCurrencyRate(String sourceCurrency, String targetCurrency, String json) throws Exception
    {
        if ( Strings.isNullOrEmpty( json ) )
            return null;
        try
        {
            InfoResponse responseObject = mapper.readValue( json, InfoResponse.class );

            if ( responseObject.response.equals( "Error" ) )
                throw new Exception( responseObject.message );

            for ( Data data: responseObject.dataList )
            {
                return new CurrencyRate( Instant.ofEpochSecond( data.time ), SysCurrency.find( sourceCurrency ),
                        SysCurrency.find( targetCurrency ), (float) data.open );
            }
        }
        catch ( IOException e )
        {
        }

        return null;
    }

    private List<CurrencyRate> jsonToCurrencyRates(String json) throws Exception
    {
        if ( Strings.isNullOrEmpty( json ) )
            return null;
        try
        {
            List<CurrencyRate> currencyRates = new ArrayList<>();
            Instant now = Instant.now();

            Map<String, Map<String, Double>> sourceCurrencies = mapper.readValue( json,
                    new TypeReference<Map<String, Map<String, Double>>>()
                    {
                    } );

            for ( String sourceCurrencyStr: sourceCurrencies.keySet() )
            {
                Map<String, Double> targetCurrencies = sourceCurrencies.get( sourceCurrencyStr );

                for ( String targetCurrencyStr: targetCurrencies.keySet() )
                {
                    Double value = targetCurrencies.get( targetCurrencyStr );

                    CurrencyRate currencyRate = new CurrencyRate( now, SysCurrency.find( sourceCurrencyStr ),
                            SysCurrency.find( targetCurrencyStr ), value.floatValue() );

                    currencyRates.add( currencyRate );
                }
            }

            return currencyRates;
        }
        catch ( Exception e )
        {
            logger.error( "Error parsing JSON in CryptoCompare:{}", e.toString() );
        }

        return Collections.emptyList();
    }

    private List<CoinInfo> jsonToCoinInfoList(String json) throws Exception
    {
        if ( Strings.isNullOrEmpty( json ) )
            return null;
        try
        {
            List<CoinInfo> coinInfoList = new ArrayList<>();
            // Instant now = Instant.now();

            CCCoinInfoRaw rawData = mapper.readValue( json, CCCoinInfoRaw.class );

            if ( rawData != null )
            {
                for ( String sCoinCode: rawData.coins.keySet() )
                {
                    Map<String, CCCoinInfo> targetCurrencies = rawData.coins.get( sCoinCode );

                    for ( String tCoinCode: targetCurrencies.keySet() )
                    {
                        CCCoinInfo ccCoinInfo = targetCurrencies.get( tCoinCode );
                        CoinInfo coinInfo = new CoinInfo( sCoinCode, tCoinCode, ccCoinInfo.supply, ccCoinInfo.volume23h,
                                ccCoinInfo.price );
                        coinInfoList.add( coinInfo );
                    }
                }
            }
            return coinInfoList;
        }
        catch ( Exception e )
        {
            logger.error( "Error parsing JSON in CryptoCompare:{}", e.toString() );
        }

        return Collections.emptyList();
    }

    private List<CurrencyRate> jsonToCurrencyRateList(SysCurrency sourceCurrency, SysCurrency targetCurrency,
            String json) throws Exception
    {
        if ( Strings.isNullOrEmpty( json ) )
            return Collections.emptyList();

        try
        {
            List<CurrencyRate> rateList = new ArrayList<CurrencyRate>();

            InfoResponse responseObject = mapper.readValue( json, InfoResponse.class );

            if ( responseObject.response.equals( "Error" ) )
                throw new Exception( responseObject.message );

            for ( Data data: responseObject.dataList )
            {
                rateList.add( new CurrencyRate( Instant.ofEpochSecond( data.time ), sourceCurrency, targetCurrency,
                        data.open > 0 ? (float) data.open : (float) data.close ) );
            }

            return rateList;
        }
        catch ( IOException e )
        {
            return Collections.emptyList();
        }
    }

    public void setMapper(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

    public void setInfoServiceUrl(String infoServiceUrl)
    {
        this.infoServiceUrl = infoServiceUrl;
    }

}
