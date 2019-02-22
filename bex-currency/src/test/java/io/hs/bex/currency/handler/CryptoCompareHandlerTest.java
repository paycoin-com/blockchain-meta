package io.hs.bex.currency.handler;


import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;


@RunWith( MockitoJUnitRunner.class )
public class CryptoCompareHandlerTest
{
    @Mock
    RestTemplate restTemplate;

    @Spy
    ObjectMapper mapper;

    @InjectMocks
    CryptoCompareHandler cryptoCompareHandler;

    // --------------------------------------------------
    private CurrencyInfoRequest infoRequest;
    private ResponseEntity<String> response;
    private String jsonResponse;
    // --------------------------------------------------

    @Before
    public void setUp() throws Exception
    {
        jsonResponse = "{\"BTC\":{\"USD\":3975.84,\"EUR\":3496.43},\"ETH\":{\"USD\":0,\"EUR\":98.14}}";

        infoRequest = new CurrencyInfoRequest( SysCurrency.find( "BTC" ), SysCurrency.find( "USD" ) );

        response = new ResponseEntity<String>( jsonResponse, HttpStatus.OK );

        when( restTemplate.exchange( Mockito.anyString(), Mockito.eq( HttpMethod.GET ), Mockito.any(),
                Mockito.eq( String.class ) ) ).thenReturn( response );
    }

    @Test
    public void testGetLatestXRates()
    {
        List<CurrencyRate> xRates = cryptoCompareHandler.getLatestXRates( infoRequest );

        assertTrue( xRates.size() == 4 );

        System.out.println( "XRates:" + xRates.toString() );
    }

    @Ignore
    @Test
    public void testGetXRates()
    {
        CurrencyInfoRequest infoRequest = new CurrencyInfoRequest( SysCurrency.find( "BTC" ),
                SysCurrency.find( "USD" ) );

        infoRequest.setLimit( 10 );
        infoRequest.setPeriod( TimePeriod.MINUTE );
        CryptoCompareHandler cryptoCompareHandler = new CryptoCompareHandler();

        cryptoCompareHandler.setMapper( mapper );
        cryptoCompareHandler.setInfoServiceUrl( "https://min-api.cryptocompare.com" );
        cryptoCompareHandler.init();
        List<CurrencyRate> xRates = cryptoCompareHandler.getXRatesBy( infoRequest );
        System.out.println( "XRatesBy:" + xRates.toString() );

    }
}
