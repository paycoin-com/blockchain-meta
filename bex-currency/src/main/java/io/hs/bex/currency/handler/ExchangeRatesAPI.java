package io.hs.bex.currency.handler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.service.api.CurrencyInfoService;

@JsonIgnoreProperties( ignoreUnknown = true )
class XRatesResponse
{
    public XRatesResponse(){}
    
    @JsonProperty("rates")
    public Map<String, Double> rates;
    
    @JsonProperty("base")
    public String baseCurrency;
}

@Service("ExchangeRatesAPI")
public class ExchangeRatesAPI implements CurrencyInfoService
{

    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( ExchangeRatesAPI.class );
    // ---------------------------------

    private final String apiUrl = "https://api.exchangeratesapi.io";
    
    @PostConstruct
    public void init()
    {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0");
    }
    
    @Autowired
    private ObjectMapper mapper;
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    
    @Override
    public CurrencyRate getXRate( String sourceCurrency, String targetCurrency )
    {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public List<CurrencyRate> getXRatesBy( CurrencyInfoRequest request )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CurrencyRate> getLatestXRates( CurrencyInfoRequest request )
    {
        String url = apiUrl + "/latest?base=" + request.joinSourceCurrencies( "," )
        + "&symbols=" + request.joinTargetCurrencies( "," );
        
        try 
        {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET,entity, String.class);
            
            return jsonToCurrencyRates( response.getBody() );
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting Currency rate from:{}",url, e );
            
            return null;
        }
    }
    
    
    private List<CurrencyRate> jsonToCurrencyRates( String json ) 
            throws Exception 
    {
        if(Strings.isNullOrEmpty( json ))
            return null;
        try
        {
            List<CurrencyRate>  currencyRates = new ArrayList<>();
            Instant now = Instant.now();
            
            XRatesResponse infoResponse =  mapper.readValue( json, XRatesResponse.class );
            
            for( String targetCurrencyStr : infoResponse.rates.keySet() ) 
            {
                Double value = infoResponse.rates.get( targetCurrencyStr );
                
                CurrencyRate currencyRate = new CurrencyRate( now, SysCurrency.find( infoResponse.baseCurrency ) , 
                        SysCurrency.find( targetCurrencyStr ), value.floatValue());
                
                currencyRates.add( currencyRate );
            }
            
            return currencyRates;
        }
        catch( Exception e )
        {
            logger.error( "Error parsing JSON in ExchangeRatesAPI:{}", e.toString() );
        }
        
        return Collections.emptyList();
    }
}
