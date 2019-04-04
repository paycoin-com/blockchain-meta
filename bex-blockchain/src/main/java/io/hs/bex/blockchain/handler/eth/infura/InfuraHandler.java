package io.hs.bex.blockchain.handler.eth.infura;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;

import io.hs.bex.blockchain.model.Coin;
import io.hs.bex.blockchain.model.FeeRate;


@Service( "InfuraHandler" )
@Scope( "prototype" )
public class InfuraHandler
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( InfuraHandler.class );
    // ---------------------------------

    private JsonRpcHttpClient rpcHttpClient;
    private InfuraAPI infuraAPI;

    private String apiUrl;
    private String apiKey;

    public void init( String apiUrl, String apiKey )
    {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;

        // restTemplate = new RestTemplate();
        // headers = new HttpHeaders();
        // headers.add( "user-agent", "Mozilla/5.0" );

        rpcHttpClient = jsonRpcHttpClient();
        infuraAPI = initInfuraRPCAPI( rpcHttpClient );
    }

    private JsonRpcHttpClient jsonRpcHttpClient()
    {
        String urlStr = apiUrl + "/" + apiKey;

        URL url = null;
        Map<String, String> map = new HashMap<>();
        try
        {
            url = new URL( urlStr );
        }
        catch( Exception e )
        {

        }
        return new JsonRpcHttpClient( url, map );
    }

    private InfuraAPI initInfuraRPCAPI( JsonRpcHttpClient jsonRpcHttpClient )
    {
        return ProxyUtil.createClientProxy( getClass().getClassLoader(), InfuraAPI.class, jsonRpcHttpClient );
    }

    public FeeRate getEstimedFeeRate()
    {
        try
        {
            String response = infuraAPI.eth_gasPrice();
            int price = Coin.getAsGwei(Long.decode( response ));
            int lPrice = (int) Math.ceil( (float)price/2 );
            
            return new FeeRate( lPrice == 0?1:lPrice, price, price * 2 );
        }
        catch( Exception e )
        {
            logger.error( "Error getting estimated fee from:", e );
        }        
        catch( Throwable e )
        {
            logger.error( "Error getting estimated fee from:", e );
        }

        return new FeeRate( -1, -1, -1 );
    }

    public InfuraAPI getInfuraAPI()
    {
        return infuraAPI;
    }


}
