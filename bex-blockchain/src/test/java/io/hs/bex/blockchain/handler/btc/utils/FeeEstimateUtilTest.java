package io.hs.bex.blockchain.handler.btc.utils;



import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;


@RunWith( MockitoJUnitRunner.class )
public class FeeEstimateUtilTest
{
    @Mock
    private Appender<ILoggingEvent> mockAppender;
    
    @Mock
    private BcoinHandler bcoinHandler;

    @Spy
    private ObjectMapper mapper;
    

    //String apiUrl = "https://btc.horizontalsystems.xyz";

    //@InjectMocks
    //private FeeEstimateUtil feeEstimateUtil;

    
    @Before
    public void before() throws Exception
    {
    }
    
    //@Ignore
    @Test
    public void testFeeEstimationProcess() throws Exception
    {
//        //------------------------------------------------------
//        when( bcoinHandler.getMempoolInfo() )
//        .thenAnswer( new Answer<MempoolInfo>()
//        {
//            public MempoolInfo answer( InvocationOnMock invocation ) throws Throwable
//            {
//                return testData.getMemPoolInfo();
//            }
//        } );
//        
//        
//        when(  bcoinHandler.getMempoolTxs( Mockito.anyInt() ))
//        .thenAnswer( new Answer<List<MempoolTx>>()
//        {
//            public List<MempoolTx> answer( InvocationOnMock invocation ) throws Throwable
//            {
//                return testData.getMempoolTxData();
//            }
//        } );
        
        //------------------------------------------------------
//        FeeEstimateUtil feeEstimateUtil = new FeeEstimateUtil( bcoinHandler, 6 );
//        feeEstimateUtil.getEstimatedFee( 10 );
//        
//        TimeUnit.SECONDS.sleep( 30000 );
    }
    
    private double getFilteredSum( List<MempoolTx> memPoolTxs, double minValue, double maxValue )
    {
        return memPoolTxs.stream().filter( o -> ( minValue < o.getFeeRate() && o.getFeeRate() <= maxValue) )
                .mapToDouble( o -> o.getSize() ).sum();
    }
    
    
    @Test
    public void testFeeSum()
    {
        List<MempoolTx> memPoolTxs = new ArrayList<>();

        memPoolTxs.add( new MempoolTx( "", 10, 0.000010, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 10, 0.000011, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 10, 0.000012, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 10, 0.000013, 0 ) );
        
        memPoolTxs.add( new MempoolTx( "", 20, 0.0000810, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 20, 0.0000811, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 20, 0.0000812, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 20, 0.0000813, 0 ) );
        
        
        memPoolTxs.add( new MempoolTx( "", 30, 0.0001810, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 30, 0.0001850, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 30, 0.0001870, 0 ) );
        memPoolTxs.add( new MempoolTx( "", 30, 0.0001890, 0 ) );
        
        System.out.println( "----------------------" );
        System.out.println( "Size 10 : " + getFilteredSum( memPoolTxs, 90, 130 ) );
        System.out.println( "Size 20 : " + getFilteredSum( memPoolTxs, 400, 410 ) );
        System.out.println( "Size 30 : " + getFilteredSum( memPoolTxs, 600, 700 ) );
        
        System.out.println( "---------------");
        for(MempoolTx mempoolTx: memPoolTxs) 
        {
            System.out.println( "Size : " + mempoolTx.getSize() + " - " + mempoolTx.getFeeRate() );
        }

    }
    

}
