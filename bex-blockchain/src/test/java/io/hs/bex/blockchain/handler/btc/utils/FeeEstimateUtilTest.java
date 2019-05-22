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
        FeeEstimateUtil feeEstimateUtil = new FeeEstimateUtil( bcoinHandler, 6 );
        feeEstimateUtil.getEstimatedFee( 10 );
        
        TimeUnit.SECONDS.sleep( 30000 );
    }

    


}
