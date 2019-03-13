package io.hs.bex.blockchain.handler.eth.infura;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



public class InfuraHandlerTest
{
    String apiUrl = "https://mainnet.infura.io/v3"; 
    String apiKey = "0355dc03ff5746c79181508539b09c2c"; 
    
    InfuraHandler infuraHandler;
            
    @Before
    public void setUp() throws Exception
    {
        infuraHandler = new InfuraHandler();
    }

    @Ignore
    @Test
    public void test()
    {
        infuraHandler.init( apiUrl, apiKey );
        String hex = infuraHandler.getInfuraAPI().eth_gasPrice();
        
        System.out.print( "Hex:" + hex );
        
        System.out.print( "Int:" + Long.decode( hex ) );
        
    }

}
