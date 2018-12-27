package io.hs.bex.blockchain.handler.btc.utils;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.FeeEstimateData;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;
import io.hs.bex.blockchain.model.FeeRate;


@RunWith( MockitoJUnitRunner.class )
public class FeeEstimateUtilTest
{
    @Mock
    private Appender<ILoggingEvent> mockAppender;
    
    @Mock
    private BcoinHandler bcoinHandler;

    @Spy
    private ObjectMapper mapper;
    
    private FeeEstimateTestData testData;

    String apiUrl = "https://btc.horizontalsystems.xyz";

    //@InjectMocks
    //private FeeEstimateUtil feeEstimateUtil;

    public FeeEstimateUtilTest()
    {
        testData = new FeeEstimateTestData();
    }

    @Before
    public void before() throws Exception
    {
    }
    
    @Ignore
    @Test
    public void testActualFeeEstimationProcess() throws Exception
    {
        BcoinHandler actBcoinHandler = new BcoinHandler();
        actBcoinHandler.setMapper( mapper );
        actBcoinHandler.init( "https://btc.horizontalsystems.xyz" );
      
        FeeEstimateUtil actFeeEstimateUtil;      
        actFeeEstimateUtil = new FeeEstimateUtil( actBcoinHandler );

        actFeeEstimateUtil.getEsimatedFee( 10 );
        TimeUnit.SECONDS.sleep( 30000 );
    }
    
    //@Ignore
    @Test
    public void testFeeEstimationProcess() throws Exception
    {
        //------------------------------------------------------
        when( bcoinHandler.getMempoolInfo() )
        .thenAnswer( new Answer<MempoolInfo>()
        {
            public MempoolInfo answer( InvocationOnMock invocation ) throws Throwable
            {
                return testData.getMemPoolInfo();
            }
        } );
        
        
        when(  bcoinHandler.getMempoolTxs( Mockito.anyInt() ))
        .thenAnswer( new Answer<List<MempoolTx>>()
        {
            public List<MempoolTx> answer( InvocationOnMock invocation ) throws Throwable
            {
                return testData.getMempoolTxData();
            }
        } );
        
        //------------------------------------------------------
        FeeEstimateUtil feeEstimateUtil = new FeeEstimateUtil( bcoinHandler, 6 );
        feeEstimateUtil.getEsimatedFee( 10 );
        
        TimeUnit.SECONDS.sleep( 30000 );
    }

    
    @Ignore
    @Test
    public void testEstimateFee() throws Exception
    {
        List<Integer> predictedValues = new ArrayList<>();
        
        predictedValues.add( 200000 );
        predictedValues.add( 500000 );
        predictedValues.add( 200000 ); // Tx to be included, Ranges = {800 > 500}
        predictedValues.add( 300000 );
        predictedValues.add( 100000 );
        predictedValues.add( 100000 );
        
        FeeEstimateUtil feeEstimateUtil;
        FeeEstimateData feeEstimateData = new FeeEstimateData();
        
        for( int x=0;x < predictedValues.size(); x++ ) 
        {
            feeEstimateData.getBlocksSizeDiff().add( 0 );
        }
        
        feeEstimateUtil = new FeeEstimateUtil( bcoinHandler, feeEstimateData, 0 );

        
        FeeRate feeRate = feeEstimateUtil.estimeFee( predictedValues );
        
        assertTrue( feeRate.highPriorityRate == 800);
    }


}
