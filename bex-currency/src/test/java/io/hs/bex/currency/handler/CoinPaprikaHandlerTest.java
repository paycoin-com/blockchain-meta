package io.hs.bex.currency.handler;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;


@RunWith( MockitoJUnitRunner.class )
public class CoinPaprikaHandlerTest
{
    @Spy
    private ObjectMapper mapper;
    
    @InjectMocks
    CoinPaprikaHandler coinPaprikaHandler;
    
    //--------------------------------------------------
    private CurrencyInfoRequest infoRequest;
    //private ResponseEntity<String> response;
    //private String jsonResponse;
    //--------------------------------------------------

    
    @Before
    public void setUp() throws Exception
    {
        coinPaprikaHandler.init();
        
        infoRequest = new CurrencyInfoRequest( SysCurrency.find( "BTC" ),SysCurrency.find( "USD" ));
    }
    
    @Ignore
    @Test
    public void testGetCurrencyRate()
    {
        CurrencyRate rate = coinPaprikaHandler.getXRate( SysCurrency.BTC.getCode(), SysCurrency.USD.getCode() );
        
        assertNotNull( rate );
    }
    
    @Ignore
    @Test
    public void testGetLatesCurrencyRate()
    {
        infoRequest.getSourceCurrencies().add( SysCurrency.ETH );
        infoRequest.getSourceCurrencies().add( SysCurrency.BCH );
        infoRequest.getSourceCurrencies().add( SysCurrency.DAI );
        infoRequest.getSourceCurrencies().add( SysCurrency.BAT );
        
        List<CurrencyRate> rates = coinPaprikaHandler.getLatestXRates( infoRequest );
        
        System.out.println( "XRates:" + rates. toString());
        
        assertNotNull( rates );
    }
    
    @Test
    public void testGetCurrencyRates()
    {
        infoRequest.getSourceCurrencies().add( SysCurrency.ETH );
        infoRequest.getSourceCurrencies().add( SysCurrency.BCH );
        infoRequest.setLimit( 15 );
        infoRequest.setPeriod( TimePeriod.DAY );
        infoRequest.setDateTo(Instant.now());
        
        List<CurrencyRate> rates = coinPaprikaHandler.getXRatesBy( infoRequest );
        
        System.out.println( "XRates:" + rates. toString());
        
        assertNotNull( rates );
    }
    

}
