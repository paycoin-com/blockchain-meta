package io.hs.bex.currency.handler;


import static org.junit.Assert.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    // --------------------------------------------------
    private CurrencyInfoRequest infoRequest;
    // private ResponseEntity<String> response;
    // private String jsonResponse;
    // --------------------------------------------------
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone( ZoneId.of( "UTC" ) );


    @Before
    public void setUp() throws Exception
    {
        coinPaprikaHandler.init();

        infoRequest = new CurrencyInfoRequest( SysCurrency.find( "BTC" ), SysCurrency.find( "USD" ) );
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

        System.out.println( "XRates:" + rates.toString() );

        assertNotNull( rates );
    }

    @Ignore
    @Test
    public void testGetCurrencyRatesDays()
    {
        infoRequest.getSourceCurrencies().add( SysCurrency.ETH );
        infoRequest.getSourceCurrencies().add( SysCurrency.BCH );
        infoRequest.setLimit( 1 );
        infoRequest.setPeriod( TimePeriod.DAY );
        infoRequest.setDateTo( Instant.from( DATE_TIME_FORMATTER.parse( "2019-02-01 10:00" ) ));

        List<CurrencyRate> rates = coinPaprikaHandler.getXRatesBy( infoRequest );

        for( CurrencyRate rate : rates) 
        {
            System.out.println( "C:" + rate.getCurrency() + " Rate:" + rate.getRate() + " Date:" + rate.getDateStr() );
        }
        
        assertNotNull( rates );
      
    }
    
    @Ignore
    @Test
    public void testGetCurrencyRatesMinutes()
    {
        infoRequest.getSourceCurrencies().add( SysCurrency.ETH );
        infoRequest.getSourceCurrencies().add( SysCurrency.BCH );
        infoRequest.setLimit( 2 );
        infoRequest.setPeriod( TimePeriod.MINUTE );
        infoRequest.setDateTo( Instant.from( DATE_TIME_FORMATTER.parse( "2019-02-18 10:32" ) ));

        List<CurrencyRate> rates = coinPaprikaHandler.getXRatesBy( infoRequest );

        for( CurrencyRate rate : rates) 
        {
            System.out.println( "C:" + rate.getCurrency() + " Rate:" + rate.getRate() + " Date:" + rate.getDateStr() );
        }
        
        assertNotNull( rates );
      
    }
    
//    @Test
//    public void testGetCurrencyRates2()
//    {
//       
//        Instant temp = StringUtils.stringToInstant( "2019-02-19 23:59" );
//       
//        System.out.println( "T1:" + StringUtils.instantToString( temp ));
//        System.out.println( "T2:" + StringUtils.instantToString( temp.minus( Duration.ofDays( 1  ) )));
//        System.out.println( "T3:" + StringUtils.instantToString( temp.minus( Duration.ofDays( 2  ) )));
//
//    }

}
