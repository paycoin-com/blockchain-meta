package io.hs.bex.blockchain.handler.btc;


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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import io.hs.bex.blockchain.model.FeeRate;

//-------------------------------------
@JsonIgnoreProperties( ignoreUnknown = true )
class Response
{
  public Response(){}
  
  @JsonProperty("id")
  public String id;
  
  @JsonProperty("error")
  public ErrorResponse error;
  
  @JsonProperty("result")
  public EstimeFeeResponse result;
  
}

@JsonIgnoreProperties( ignoreUnknown = true )
class EstimeFeeResponse
{
  public EstimeFeeResponse(){}
  
  @JsonProperty("fee")
  public double fee = 0;
  
  @JsonProperty("blocks")
  public int blocks = 0;
}

@JsonIgnoreProperties( ignoreUnknown = true )
class ErrorResponse
{
  public ErrorResponse(){}
  
  @JsonProperty("message")
  public String message;
  
  @JsonProperty("code")
  public int code = 0;
}


@Service("BcoinHandler")
@Scope("prototype")
public class BcoinHandler
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BcoinHandler.class );
    // ---------------------------------

    private RestTemplate restTemplate;
    private HttpHeaders headers;
    
    @Autowired
    private ObjectMapper mapper;

    private String apiUrl;
    
    
    public void init( String apiUrl ) 
    {
        this.apiUrl = apiUrl;
        
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0");

    }
    
    public FeeRate getEstimedFeeRate() 
    {
        String url = apiUrl + "/" + "";
        
        try 
        {
            String body = "{\"method\":\"estimatesmartfee\",\"params\":[8]}";
            HttpEntity<String> entity = new HttpEntity<String>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST,entity, String.class);
            double highPriority = jsonToFeeRate( response.getBody() );

            body = "{\"method\":\"estimatesmartfee\",\"params\":[3]}";
            entity = new HttpEntity<String>(body, headers);
            response = restTemplate.exchange( url, HttpMethod.POST,entity, String.class);
            double mediumPriority = jsonToFeeRate( response.getBody());
            
            body = "{\"method\":\"estimatesmartfee\",\"params\":[1]}";
            entity = new HttpEntity<String>(body, headers);
            response = restTemplate.exchange( url, HttpMethod.POST,entity, String.class);
            double lowPriority = jsonToFeeRate( response.getBody());
            
            return new FeeRate( highPriority, mediumPriority, lowPriority );
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting estimated fee from:{}", url, e );
            
            return new FeeRate(-1,-1,-1);
        }
    }
    
    
    private double jsonToFeeRate( String json ) 
    {
        if(Strings.isNullOrEmpty( json ))
            return 0;
        
        try
        {
            Response response = mapper.readValue( json, Response.class );
            
            if( response.error == null ) 
            {
                return response.result.fee;
            }
        }
        catch( Exception e )
        {
        }
        
        return -1;
    }

}
