package io.hs.bex.currency.handler;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import io.hs.bex.common.utils.StringUtils;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.service.api.CurrencyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Service("CoinDeskInfoService")
public class CoinDeskInfoHandler implements CurrencyInfoService
{
    
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( CoinDeskInfoHandler.class );
    // ---------------------------------
    
    @Value("${service.ccy.coindesk.api.url}")
    private String infoServiceUrl;
    
    private RestTemplate restTemplate;
    private ObjectMapper mapper;
    private HttpHeaders headers;
    
    @PostConstruct
    public void init()
    {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0");
        mapper = new ObjectMapper();
    }
    
    @Override
    public CurrencyRate getCurrencyRate( String sourceCurrency,String targetCurrency ) 
    {
        String url = infoServiceUrl + "/v1/bpi/currentprice.json";
        
        try 
        {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET,entity, String.class);
            
            return jsonToCurrencyRate( response.getBody() );
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting Currency rate from:{}",url, e );
            
            return null;
        }
    }
    
    @Override
    public List<CurrencyRate> getCurrencyRateBy( CurrencyInfoRequest request ) 
    {
        String url = infoServiceUrl + "/v1/bpi/historical/close.json";
        
        try 
        {
            if( request.getPeriod() == TimePeriod.YEAR ) 
            {
                LocalDate date = request.getDateTo().atZone(ZoneId.systemDefault()).toLocalDate();
                
                String startDate = date.getDayOfYear() + "-01" + "-01"; 
                String endDate = date.getDayOfYear() + "-12" + "-31"; 
                
                url += "?start=" + startDate + "&end=" + endDate;
            }
                
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET,entity, String.class);
            
            return jsonToCurrencyRateList( response.getBody() );
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting Currency rate list from:{}", url, e );
            
            return Collections.emptyList();
        }
    }
    
    @SuppressWarnings( "unchecked" )
    private CurrencyRate jsonToCurrencyRate( String json ) 
    {
        if(Strings.isNullOrEmpty( json ))
            return null;
        
        try
        {
            Object responseObject = mapper.readValue( json, Object.class);
            
            Map<String,?> obj = (Map<String, ?>) responseObject;
            Map<String,?> usd = (Map<String, ?>)((Map<String, ?>) obj.get( "bpi" )).get( "USD" );

            double rate = (Double) usd.get( "rate_float" );
            
            return new CurrencyRate( SysCurrency.BTC , SysCurrency.USD, (float)rate);
        }
        catch( IOException e )
        {
            return null;
        }
    }
    
    
    @SuppressWarnings( "unchecked" )
    private List<CurrencyRate> jsonToCurrencyRateList( String json ) 
    {
        if(Strings.isNullOrEmpty( json ))
            return Collections.emptyList();
        
        try
        {
            List<CurrencyRate> rateList = new ArrayList<CurrencyRate>(); 
            
            Object responseObject = mapper.readValue( json, Object.class);
            
            Map<String,?> obj = (Map<String, ?>) responseObject;
            Map<String,?> bpi = (Map<String, ?>) obj.get( "bpi" );
            
            for(String rateDateStr : bpi.keySet()) 
            {
                double rate = 0;
                
                if(bpi.get( rateDateStr ).getClass().getName().equals( "java.lang.Double" ))
                    rate = (Double) bpi.get( rateDateStr );
                else if(bpi.get( rateDateStr ).getClass().getName().equals( "java.lang.Integer" ))
                    rate = (Integer) bpi.get( rateDateStr );
                
                Instant date = StringUtils.stringToInstant( rateDateStr, "yyyy-MM-d");
                rateList.add( new CurrencyRate( date, SysCurrency.BTC, SysCurrency.USD, (float)rate));
            }
            
            return rateList;
        }
        catch( IOException e )
        {
            return Collections.emptyList();
        }
    }

 
}
