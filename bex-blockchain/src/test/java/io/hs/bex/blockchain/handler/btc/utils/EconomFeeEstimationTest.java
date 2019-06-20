package io.hs.bex.blockchain.handler.btc.utils;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.blockchain.dao.FeeRateDataDAO;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;

@RunWith( MockitoJUnitRunner.class )
public class EconomFeeEstimationTest
{
    @Before
    public void setUp() throws Exception
    {}
    
    @Spy
    private ObjectMapper mapper;
    
    @Mock
    FeeRateDataDAO feeRateDataDAO;
    
    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();
    
    BcoinHandler actBcoinHandler = new BcoinHandler();


    @Ignore
    @Test
    public void testFeeEstimationProcess() throws Exception
    {
        actBcoinHandler.setMapper( mapper );
        actBcoinHandler.init( "http://btc-testnet.horizontalsystems.xyz/apg" );
        
        startInfoFetchTask( 20, 180 );
      
    }
    
    
    private void startInfoFetchTask( int startAfter, int period )
    {
        try
        {
            timerService.scheduleWithFixedDelay( () -> fethBlockInfo(), startAfter, period, TimeUnit.SECONDS );
        }
        catch( Exception e )
        {
            System.out.println( "Error starting test thread:" + e );
        }
    }
    
    private void fethBlockInfo()
    {
        try 
        {
            int blockHeight = getLastBlockHeight();
            
            System.out.println( "BlockHeight:" + blockHeight );
        }
        catch( Exception  e ) 
        {
            // Thread should retry operation 
            System.out.println( "Error fetching data from Bcoin:" + e );
        }
    }
    
    private int getLastBlockHeight()
    {
        return actBcoinHandler.getPeerInfo().getChainInfo().getLastBlockHeight();
    }

}
