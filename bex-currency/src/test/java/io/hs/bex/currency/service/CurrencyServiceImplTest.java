package io.hs.bex.currency.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import io.hs.bex.common.utils.StringUtils;
import io.hs.bex.currency.model.CurrencyInfoRequest;
import io.hs.bex.currency.model.CurrencyRate;
import io.hs.bex.currency.model.SysCurrency;
import io.hs.bex.currency.model.TimePeriod;
import io.hs.bex.currency.service.api.CurrencyInfoService;
import io.hs.bex.datastore.service.api.DataStoreService;


@RunWith( MockitoJUnitRunner.class )
public class CurrencyServiceImplTest
{
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Mock
    DataStoreService dataStoreService;

    @Mock
    CurrencyInfoService defaultInfoService;

    @Spy
    ObjectMapper mapper;

    @InjectMocks
    CurrencyServiceImpl currencyService;

    private String xratesContentUSD = "{\"54\":\"100\",\"55\":\"101\",\"56\":\"102\",\"57\":\"103\"}";
    private String xratesContentEUR = "{\"54\":\"3.0\",\"55\":\"3.1\",\"56\":\"3.2\",\"57\":\"3.3\"}";

    private String currencies = "  [{" + "    \"code\": \"USD\"," + "    \"symbol_ucode\": \"U+0024\","
            + "    \"type\": \"FIAT\"," + "    \"details\": null," + "    \"code_numeric\": 840,"
            + "    \"display_name\": \"US Dollar\"" + "  },{" + "    \"code\": \"EUR\","
            + "    \"symbol_ucode\": \"U+0024\"," + "    \"type\": \"FIAT\"," + "    \"details\": null,"
            + "    \"code_numeric\": 841," + "    \"display_name\": \"EURO\"" + "  }," + "  {"
            + "    \"code\": \"BTC\"," + "    \"symbol_ucode\": \"U+20BF\"," + "    \"type\": \"DIGITAL\","
            + "    \"details\": null," + "    \"code_numeric\": 1," + "    \"display_name\": \"Bitcoin\"" + "  }]";

    private List<CurrencyRate> currencyRatesUSD = new ArrayList<>();
    private List<CurrencyRate> currencyRatesEUR = new ArrayList<>();

    private Map<Integer, Float> savedData = null;

    private List<CurrencyRate> createCurrencyRatesList( SysCurrency currency, float value, String date )
    {
        CurrencyRate cr = new CurrencyRate();
        cr.setTargetCurrency( currency );
        cr.setRate( value );
        cr.setDate( StringUtils.stringToInstant( date ) );
        cr.setCurrency( SysCurrency.BTC );
        
        if(currency == SysCurrency.USD) 
            currencyRatesUSD.add( cr );
        else 
            currencyRatesEUR.add( cr );

        return currencyRatesUSD;
    }

    @Before
    public void setUp() throws Exception
    {
        createCurrencyRatesList( SysCurrency.USD, (float) 104, "2018-12-30 23:58" );
        createCurrencyRatesList( SysCurrency.USD, (float) 105, "2018-12-30 23:59" );
        createCurrencyRatesList( SysCurrency.USD, (float) 106, "2018-12-31 00:00" );
        createCurrencyRatesList( SysCurrency.USD, (float) 107, "2018-12-31 00:01" );
        createCurrencyRatesList( SysCurrency.USD, (float) 108, "2018-12-31 00:02" );
        createCurrencyRatesList( SysCurrency.USD, (float) 109, "2018-12-31 00:03" );
        createCurrencyRatesList( SysCurrency.USD, (float) 110, "2018-12-31 00:04" );
        
        createCurrencyRatesList( SysCurrency.EUR, (float) 3.4, "2018-12-30 23:58" );
        createCurrencyRatesList( SysCurrency.EUR, (float) 3.5, "2018-12-30 23:59" );
        createCurrencyRatesList( SysCurrency.EUR, (float) 3.6, "2018-12-31 00:00" );
        createCurrencyRatesList( SysCurrency.EUR, (float) 3.7, "2018-12-31 00:01" );
        createCurrencyRatesList( SysCurrency.EUR, (float) 3.8, "2018-12-31 00:02" );
        createCurrencyRatesList( SysCurrency.EUR, (float) 3.9, "2018-12-31 00:03" );
        createCurrencyRatesList( SysCurrency.EUR, (float) 4.0, "2018-12-31 00:04" );

        //when( defaultInfoService.getXRatesBy( Mockito.any() ) ).thenReturn( currencyRates );
        
        when( defaultInfoService.getXRatesBy( Mockito.any() ) )
        .thenAnswer( new Answer<List<CurrencyRate>>() {
            public List<CurrencyRate> answer( InvocationOnMock invocation ) throws Throwable
            {
                CurrencyInfoRequest request = invocation.getArgument( 0 );

                System.out.println( "getXRatesBy:" + request.getSourceCcyCode() );

                if("USD".equals( request.getTargetCcyCode() )) 
                {
                    return currencyRatesUSD;
                }
                else
                    return currencyRatesEUR;
            }
        } );

       
        when( dataStoreService.getFileContent( Mockito.anyString(), Mockito.anyString() ) )
                .thenAnswer( new Answer<String>() {
                    public String answer( InvocationOnMock invocation ) throws Throwable
                    {
                        String path = invocation.getArgument( 0 );
                        String out  = "";

                        System.out.println( "Get File Content:" + path );

                        if( Strings.isNullOrEmpty( path ) || path.equals( "/xrates" ) )
                            return currencies;

                        if( path.contains( "USD" ) )
                        {
                            out = xratesContentUSD;
                            xratesContentUSD = "";
                        }
                        else 
                        {
                            out = xratesContentEUR;
                            xratesContentEUR = "";
                        }
                        
                        return out;
                    }
                } );

        when( dataStoreService.saveFile( Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString() ) ).thenAnswer( new Answer<Void>() {
                    public Void answer( InvocationOnMock invocation ) throws Throwable
                    {
                        String path = invocation.getArgument( 1 );
                        String data = invocation.getArgument( 3 );

                        System.out.println( "Saving Data Path:" + path );
                        System.out.println( "Saving Data:" + data );

                        savedData = mapper.readValue( data, new TypeReference<LinkedHashMap<Integer, Float>>() {} );

                        return null;
                    }
                } );

    }

    @Test
    public void testFetchAndStore()
    {
        currencyService.buildTaskParams();
        CurrencyInfoRequest request = new CurrencyInfoRequest();
        request.setLimit( 3 );
        request.setDateTo( Instant.now() );
        request.setPeriod( TimePeriod.MINUTE );
        request.getSourceCurrencies().add( SysCurrency.BTC );
        //List<SysCurrency> currencies = currencyService. currencyTaskParams.getTargetCurrencies();
        currencyService.savePeriodicXRates( request );

        assertEquals( savedData.size(), 7 ); // Check If Second date included or
                                             // not
    }

}
