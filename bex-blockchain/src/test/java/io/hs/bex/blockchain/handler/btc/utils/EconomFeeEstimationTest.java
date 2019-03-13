package io.hs.bex.blockchain.handler.btc.utils;


import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.blockchain.handler.btc.BcoinHandler;

@RunWith( MockitoJUnitRunner.class )
public class EconomFeeEstimationTest
{

    @Before
    public void setUp() throws Exception
    {}
    
    @Spy
    private ObjectMapper mapper;

    //@Ignore
    @Test
    public void testActualFeeEstimationProcess() throws Exception
    {
        BcoinHandler actBcoinHandler = new BcoinHandler();
        actBcoinHandler.setMapper( mapper );
        actBcoinHandler.init( "https://btc.horizontalsystems.xyz/apg" );
      
        EconomFeeEstimation eFeeEstimateUtil;      
        eFeeEstimateUtil = new EconomFeeEstimation( actBcoinHandler );

        //eFeeEstimateUtil.getEsimatedFee( 10 );
        TimeUnit.SECONDS.sleep( 30000 );
    }

}
