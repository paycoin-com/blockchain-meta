package io.hs.bex.currency.bitcoin.service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;



public class BitcoinInfoServiceTest
{
    private String infoServiceUrl1 = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private String infoServiceUrl2 = "https://api.coindesk.com/v1/bpi/historical/close.json";

    ObjectMapper mapper = new ObjectMapper();

    @Ignore
    @SuppressWarnings( "unchecked" )
    @Test
    public void testInfoService()
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add( "user-agent", "Mozilla/5.0" );
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );

            ResponseEntity<String> response = restTemplate.exchange( infoServiceUrl1, HttpMethod.GET, entity,
                    String.class );

            if( response != null )
            {
                Object myObjects = mapper.readValue( response.getBody(), Object.class );

                Map<String, ?> obj = (Map<String, ?>) myObjects;
                Map<String, ?> usd = (Map<String, ?>) ( (Map<String, ?>) obj.get( "bpi" )).get( "USD" );

                double rate =  (Double) usd.get( "rate_float" );
                System.out.println( "Response USD Rate:" +  (float)rate );

            }
            else
            {
                System.out.println( "Response is NULL" );
            }

        }
        catch( Exception e )
        {
            System.out.println( "Error:" + e.toString() );
        }

    }

    @Ignore
    @SuppressWarnings( { "unchecked", "unused" } )
    @Test
    public void testInfoServiceHistorical()
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add( "user-agent", "Mozilla/5.0" );
            HttpEntity<String> entity = new HttpEntity<String>( "parameters", headers );

            ResponseEntity<String> response = restTemplate.exchange( infoServiceUrl2, HttpMethod.GET, entity,
                    String.class );

            if( response != null )
            {
                Object myObjects = mapper.readValue( response.getBody(), Object.class );

                Map<String, ?> obj = (Map<String, ?>) myObjects;
                Map<String, ?> bpi = (Map<String, ?>) ( (Map<String, ?>) obj.get( "bpi" ));

                for( String dateStr: bpi.keySet() )
                {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
                    LocalDate localDate = LocalDate.parse( dateStr, formatter );
                    ZonedDateTime zonedDateTime = localDate.atStartOfDay( ZoneId.systemDefault() );
                    
                    //System.out.println( "Response USD Rate:" + localDate + "-" + zonedDateTime );
                }

            }
            else
            {
                System.out.println( "Response is NULL" );
            }

        }
        catch( Exception e )
        {
            System.out.println( "Error:" + e.toString() );
        }
    }
    
    @Test
    public void testFormat() 
    {
        int num = 111;
        String path = String.format("%02d", num);
        
        System.out.println( "Format:" + path );

    }
}
