package io.hs.bex.currency.handler;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.hs.bex.common.utils.StringUtils;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.service.api.CurrencyInfoService;


//---------------------------------------

@JsonIgnoreProperties( ignoreUnknown = true )
class CoinPResponse
{
    @JsonProperty( "price" )
    public double price = 0;

    @JsonProperty( "timestamp" )
    public String timestamp;

    // "timestamp": "2018-03-01T00:00:00Z",
    // "price": 855.53,
    // "volume_24h": 1968587956,
    // "market_cap": 83761787514
}
// ---------------------------------------

@Service( "CoinPaprikaHandler" )
public class CoinPaprikaHandler implements CurrencyInfoService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CoinPaprikaHandler.class );
    // ---------------------------------

    private String infoServiceUrl = "https://api.coinpaprika.com";

    // Maximum requests per second allowed in Papripa API.
    private static final int MAX_REQUEST_COUNT_PER_SECOND = 8;

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
    public CurrencyRate getXRate( String sourceCurrency, String targetCurrency )
    {
        OffsetDateTime utc = OffsetDateTime.now( ZoneOffset.UTC );

        String url = infoServiceUrl + "/v1/tickers/" + SysCurrency.find( sourceCurrency ).getUid()
                + "/historical?interval=5m&limit=1" + "&quote=" + targetCurrency.toLowerCase() + "&start="
                + utc.minus( Duration.ofMinutes( 5 ) ).toEpochSecond();

        try
        {
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity, String.class );

            return jsonToCurrencyRate( sourceCurrency, targetCurrency, response.getBody() );
        }
        catch( Exception e )
        {
            logger.error( "Error getting Currency rate from:{}", url, e );

            return null;
        }
    }

    @Override
    public List<CurrencyRate> getXRatesBy( CurrencyInfoRequest request )
    {
        String url = "";
        int requestCount = 0;

        try
        {
            List<CurrencyRate> currencyRates = new ArrayList<>();

            for( SysCurrency sourceCurrency: request.getSourceCurrencies() )
            {
                url = constructUrl( request, sourceCurrency.getUid(), request.getTargetCcyCode() );

                try
                {
                    HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
                    ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity,
                            String.class );

                    jsonToCurrencyRates( sourceCurrency.getCode(), request.getTargetCcyCode(), request.getDateTo(),
                            currencyRates, response.getBody() );
                }
                catch( HttpClientErrorException e )
                {
                    logger.error( "(!!!) Error fetching data for {}:{}, (Ignoring) ", sourceCurrency.getCode(),
                            request.getTargetCcyCode() );
                    // ignore
                }

                requestCount = adjustRequestDelay( requestCount );
            }

            return currencyRates;
        }
        catch( Exception e )
        {
            logger.error( "Error getting Currency rate from:{}", url, e );

            return Collections.emptyList();
        }

    }

    @Override
    public List<CurrencyRate> getLatestXRates( CurrencyInfoRequest request )
    {
        String url = "";
        OffsetDateTime utc = OffsetDateTime.now( ZoneOffset.UTC );
        long reqTime = utc.minus( Duration.ofMinutes( 5 ) ).toEpochSecond();
        int requestCount = 0;

        try
        {
            List<CurrencyRate> currencyRates = new ArrayList<>();
            Instant fetchTime = Instant.now();

            for( SysCurrency sourceCurrency: request.getSourceCurrencies() )
            {
                url = infoServiceUrl + "/v1/tickers/" + sourceCurrency.getUid() + "/historical?interval=5m&limit=1"
                        + "&quote=" + request.getTargetCcyCode() + "&start=" + reqTime;

                try
                {
                    HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );
                    ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET, entity,
                            String.class );

                    jsonToCurrencyRates( sourceCurrency.getCode(), request.getTargetCcyCode(), fetchTime, currencyRates,
                            response.getBody() );
                }
                catch( HttpClientErrorException e )
                {
                    logger.error( "(!!!) Error fetching data for {}:{}, (Ignoring) ", sourceCurrency.getCode(),
                            request.getTargetCcyCode() );
                    // ignore
                }

                requestCount = adjustRequestDelay( requestCount );
            }

            return currencyRates;
        }
        catch( Exception e )
        {
            logger.error( "Error getting Currency rate from:{}", url, e );

            return Collections.emptyList();
        }
    }

    private String constructUrl( CurrencyInfoRequest request, String sourceCcy, String targetCcy )
    {
        String url = "";
        
        request.setDateTo( adjustDateTime( request.getPeriod(), request.getDateTo() ) );

        if( request.getPeriod() == TimePeriod.YEAR )
        {

        }
        else if( request.getPeriod() == TimePeriod.MONTH )
        {

        }
        else if( request.getPeriod() == TimePeriod.DAY )
        {
            url = infoServiceUrl + "/v1/tickers/" + sourceCcy + "/historical?interval=1d" + "&quote=" + targetCcy
                    + "&limit=" + request.getLimit() + "&start="
                    + request.getDateTo().minus( Duration.ofDays( request.getLimit() ) );
        }
        else if( request.getPeriod() == TimePeriod.HOUR )
        {
            url = infoServiceUrl + "/v1/tickers/" + sourceCcy + "/historical?interval=1h" + "&quote=" + targetCcy
                    + "&limit=" + request.getLimit() + "&start="
                    + request.getDateTo().minus( Duration.ofHours( request.getLimit() ) );
        }
        else if( request.getPeriod() == TimePeriod.MINUTE )
        {
            int limit = ( request.getLimit() / 5) + 1;
            url = infoServiceUrl + "/v1/tickers/" + sourceCcy + "/historical?interval=5m" + "&quote=" + targetCcy
                    + "&limit=" + limit + "&start="
                    + request.getDateTo().minus( Duration.ofMinutes( request.getLimit() ) );
        }

        return url;
    }

    private Instant adjustDateTime( TimePeriod timePeriod, Instant dt )
    {
        LocalDateTime ldt = LocalDateTime.ofInstant( dt, ZoneId.systemDefault() );
        ZoneOffset zof = ZoneOffset.of( ZoneId.systemDefault().getId() );
        
        if( timePeriod == TimePeriod.YEAR )
        {

        }
        else if( timePeriod == TimePeriod.MONTH )
        {
            dt = ldt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).toInstant( zof);
        }
        else if( timePeriod == TimePeriod.DAY )
        {
            dt = ldt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).toInstant( zof );
        }
        else if( timePeriod == TimePeriod.HOUR )
        {
            dt = ldt.withMinute( 0 ).withSecond( 0 ).toInstant( zof );
        }
        else if( timePeriod== TimePeriod.MINUTE )
        {
            dt = ldt.withSecond( 0 ).toInstant( zof );
        }
        
        return dt;
    }

    private List<CurrencyRate> jsonToCurrencyRates( String sourceCurrency, String targetCurrency, Instant fetchTime,
            List<CurrencyRate> currencyRates, String json ) throws Exception
    {
        if( Strings.isNullOrEmpty( json ) )
            return null;
        try
        {
            if( Strings.isNullOrEmpty( json ) )
                return null;
            try
            {
                List<CoinPResponse> responseList = mapper.readValue( json,
                        new TypeReference<List<CoinPResponse>>() {} );

                for( CoinPResponse data: responseList )
                {
                    currencyRates.add( new CurrencyRate( fetchTime, SysCurrency.find( sourceCurrency ),
                            SysCurrency.find( targetCurrency ), (float) data.price ) );

                    fetchTime = StringUtils.stringZonedToInstant( data.timestamp );
                }

            }
            catch( Exception e )
            {
                logger.error( "(!!!) Error in converting response to object:", e );
            }

            return currencyRates;
        }
        catch( Exception e )
        {
            logger.error( "Error parsing JSON in CoinPaprika:{}", e.toString() );
        }

        return Collections.emptyList();
    }

    private CurrencyRate jsonToCurrencyRate( String sourceCurrency, String targetCurrency, String json )
            throws Exception
    {
        if( Strings.isNullOrEmpty( json ) )
            return null;
        try
        {
            List<CoinPResponse> responseList = mapper.readValue( json, new TypeReference<List<CoinPResponse>>() {} );

            for( CoinPResponse data: responseList )
            {
                return new CurrencyRate( StringUtils.stringZonedToInstant( data.timestamp ),
                        SysCurrency.find( sourceCurrency ), SysCurrency.find( targetCurrency ), (float) data.price );
            }
        }
        catch( Exception e )
        {
            logger.error( "(!!!) Error in converting response to object:", e );
        }

        return null;
    }

    private int adjustRequestDelay( int requestCount ) throws InterruptedException
    {
        requestCount++;

        // Delay operation for 1 second
        if( requestCount == MAX_REQUEST_COUNT_PER_SECOND )
        {
            requestCount = 0;
            TimeUnit.SECONDS.sleep( 1 );
        }

        return requestCount;
    }

}
