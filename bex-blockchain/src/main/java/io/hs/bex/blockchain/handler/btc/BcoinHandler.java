package io.hs.bex.blockchain.handler.btc;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

import io.hs.bex.blockchain.handler.btc.model.BlockInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;
import io.hs.bex.blockchain.handler.btc.model.PeerInfo;
import io.hs.bex.blockchain.model.FeeRate;

@JsonIgnoreProperties( ignoreUnknown = true )
class MempoolTxResponse
{
    public MempoolTxResponse()
    {}
    
    @JsonProperty( "result" )
    public Map<String, MempoolTx> mempoolTxs;

}

@JsonIgnoreProperties( ignoreUnknown = true )
class MempoolInfoResponse
{
    public MempoolInfoResponse()
    {}
    
    @JsonProperty( "result" )
    public MempoolInfo memPoolInfo;

}

//-------------------------------------
@JsonIgnoreProperties( ignoreUnknown = true )
class Response
{
    public Response()
    {}

    @JsonProperty( "id" )
    public String id;

    @JsonProperty( "error" )
    public ErrorResponse error;

    @JsonProperty( "result" )
    public EstimeFeeResponse result;

}

@JsonIgnoreProperties( ignoreUnknown = true )
class EstimeFeeResponse
{
    public EstimeFeeResponse()
    {}

    @JsonProperty( "fee" )
    public double fee = 0;

    @JsonProperty( "blocks" )
    public int blocks = 0;
}

@JsonIgnoreProperties( ignoreUnknown = true )
class ErrorResponse
{
    public ErrorResponse()
    {}

    @JsonProperty( "message" )
    public String message;

    @JsonProperty( "code" )
    public int code = 0;
}

@Service( "BcoinHandler" )
@Scope( "prototype" )
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
        headers.add( "user-agent", "Mozilla/5.0" );

    }

    public FeeRate getEstimedFeeRate()
    {
        //String url = apiUrl + "/" + "";

        try
        {
//            String body = "{\"method\":\"estimatesmartfee\",\"params\":[1]}";
//            HttpEntity<String> entity = new HttpEntity<String>( body, headers );
//            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );
//            double highPriority = jsonToFeeRate( response.getBody() );
//            
//            body = "{\"method\":\"estimatesmartfee\",\"params\":[6]}";
//            entity = new HttpEntity<String>( body, headers );
//            response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );
//            double mediumPriority = jsonToFeeRate( response.getBody() );
//            
////            body = "{\"method\":\"estimatesmartfee\",\"params\":[15]}";
////            entity = new HttpEntity<String>( body, headers );
////            response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );
////            jsonToFeeRate( response.getBody() );
//            
//            double lowPriority = mediumPriority/2; 

            return new FeeRate( 1 , 2, 3 );
        }
        catch( Exception e )
        {
            logger.error( "Error getting estimated fee from:{}",  e );

            return new FeeRate( -1, -1, -1 );
        }
    }

//    private double jsonToFeeRate( String json )
//    {
//        if( Strings.isNullOrEmpty( json ) )
//            return 0;
//
//        try
//        {
//            Response response = mapper.readValue( json, Response.class );
//
//            if( response.error == null )
//            {
//                return response.result.fee;
//            }
//        }
//        catch( Exception e )
//        {}
//
//        return -1;
//    }

    public List<MempoolTx> getMempoolTxs( int verbose )
    {
        String url = apiUrl + "/" + "";

        try
        {
            String body = "{\"method\":\"getrawmempool\",\"params\":[" + verbose + "]}";
            HttpEntity<String> entity = new HttpEntity<String>( body, headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity, String.class );

            return jsonToMempoolTxs( response.getBody() );

        }
        catch( Exception e )
        {
            logger.error( "Error getting estimated fee from:{}", url, e );

            return Collections.emptyList();
        }
    }
    
    private List<MempoolTx> jsonToMempoolTxs( String json )
    {
        try
        {
            MempoolTxResponse response = mapper.readValue( json, MempoolTxResponse.class );
            
            List<MempoolTx> responsList = new ArrayList<>(response.mempoolTxs.values());
            
            return responsList;
        }
        catch( Exception e )
        {
            return Collections.emptyList();
        }
        
    }

    public MempoolInfo getMempoolInfo()
    {
        String url = apiUrl + "/";

        try
        {
            String body = "{\"method\":\"getmempoolinfo\"}";
            HttpEntity<String> entity = new HttpEntity<String>( body, headers );
            ResponseEntity<String> response = restTemplate.exchange( url, HttpMethod.POST, entity,
                    String.class );

            return jsonToMempoolInfo( response.getBody() );
        }
        catch( Exception e )
        {
            logger.error( "Error getting mempool info from:{}", url, e );

            return new MempoolInfo();
        }
    }
    
    public PeerInfo getPeerInfo()
    {
        String url = apiUrl + "/";

        try
        {
            ResponseEntity<PeerInfo> response = restTemplate.getForEntity( url, PeerInfo.class );
            return response.getBody();
        }
        catch( Exception e )
        {
            logger.error( "Error getting peer info from:{}", url, e );

            return null;
        }
    }
    
    public BlockInfo getBlock( long height )
    {
        String url = apiUrl + "/block/" + height;

        try
        {
            ResponseEntity<BlockInfo> response = restTemplate.getForEntity( url, BlockInfo.class );
            return response.getBody();
        }
        catch( Exception e )
        {
            logger.error( "Error getting block info from:{}", url, e );

            return null;
        }
    }

    
    private MempoolInfo jsonToMempoolInfo( String json )
    {
        try
        {
            MempoolInfoResponse response = mapper.readValue( json, MempoolInfoResponse.class );
            
            return response.memPoolInfo;
        }
        catch( Exception e )
        {
            return null;
        }
    }
    
    

    public void setMapper( ObjectMapper mapper )
    {
        this.mapper = mapper;
    }

}
