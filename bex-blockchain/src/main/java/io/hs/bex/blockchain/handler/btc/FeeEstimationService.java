package io.hs.bex.blockchain.handler.btc;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hs.bex.blockchain.model.FeeRate;

@Service("EarnFeeEstimation")
@Scope("prototype")
public class FeeEstimationService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( FeeEstimationService.class );
    // ---------------------------------

    private String apiUrl = "https://bitcoinfees.earn.com/api/v1/fees/recommended";
    
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    
    @Autowired
    private ObjectMapper mapper;
    
    @PostConstruct
    public void init()
    {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0");
    }
    
    
    public FeeRate getEsimatedFee( int nBlocks ) 
    {
        String url = apiUrl;
        
        try 
        {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.GET,entity, String.class);
            
            return mapper.readValue(response.getBody(),FeeRate.class);
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting Currency rate from:{}",url, e );
            
            return null;
        }
    }
    
}
