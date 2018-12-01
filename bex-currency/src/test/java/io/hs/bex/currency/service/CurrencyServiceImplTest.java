package io.hs.bex.currency.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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
import io.hs.bex.currency.model.CurrencyRate;
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
    
    private String xratesContent1 = "{\"0\":\"1.2\",\"1\":\"1.3\",\"2\":\"1.3\",\"4\":\"1.3\"}"; 
    
    private String currencies =
            "  [{" + 
            "    \"code\": \"USD\"," + 
            "    \"symbol_ucode\": \"U+0024\"," + 
            "    \"type\": \"FIAT\"," + 
            "    \"details\": null," + 
            "    \"code_numeric\": 840," + 
            "    \"display_name\": \"US Dollar\"" + 
            "  }," + 
            "  {" + 
            "    \"code\": \"BTC\"," + 
            "    \"symbol_ucode\": \"U+20BF\"," + 
            "    \"type\": \"DIGITAL\"," + 
            "    \"details\": null," + 
            "    \"code_numeric\": 1," + 
            "    \"display_name\": \"Bitcoin\"" + 
            "  }]" ; 
    
    
    private List<CurrencyRate> currencyRates = new ArrayList<>();
    
    private Map<Integer,Float> savedData = null; 
    
    private List<CurrencyRate> createCurrencyRatesList(float value, String date)
    {
        CurrencyRate cr = new CurrencyRate();
        cr.setRate( value );
        cr.setDate( StringUtils.stringToInstant( date ) );
        currencyRates.add( cr );
        
        return currencyRates;
    }

    @Before
    public void setUp() throws Exception
    {
        createCurrencyRatesList((float)3.6, "2018-11-30 12:56" );
        createCurrencyRatesList((float)3.7, "2018-11-30 12:57" );
        createCurrencyRatesList((float)3.8, "2018-11-30 12:58" );
        createCurrencyRatesList((float)3.8, "2018-11-30 12:59" );
        createCurrencyRatesList((float)3.8, "2018-11-30 13:20" );
        createCurrencyRatesList((float)3.8, "2018-11-30 13:21" );
        createCurrencyRatesList((float)3.8, "2018-11-30 13:32" );

        when( defaultInfoService.getXRatesBy( Mockito.any() )).thenReturn( currencyRates );
        
        when( dataStoreService.getFileContent( Mockito.anyString(), Mockito.anyString() ) )
            .thenAnswer( new Answer<String>()
        {
            public String answer( InvocationOnMock invocation ) throws Throwable
            {
                String path = invocation.getArgument( 0 );

                System.out.println( "Get File Content:" + path );

                if(Strings.isNullOrEmpty( path ) || path.equals( "/xrates" ))
                    return currencies;
                
                return xratesContent1;
            }
        } );

        when( dataStoreService.saveFile( Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString() ) )
        .thenAnswer( new Answer<Void>()
        {
        public Void answer( InvocationOnMock invocation ) throws Throwable
        {
            String path = invocation.getArgument( 1 );
            String data = invocation.getArgument( 3 );

            System.out.println( "Saving Data Path:" + path );
            System.out.println( "Saving Data:" + data );
            
            savedData = mapper.readValue( data, new TypeReference<LinkedHashMap<Integer,Float>>(){});
            
            return null;
        }
    } );

    }

    @Test
    public void  testFetchAndStore()
    {
        currencyService.buildTaskParams();
        currencyService.fetchAndStoreXRates( 1, TimePeriod.MINUTE, 3 );
        
        assertEquals( savedData.size(), 7 ); //Check If Second date included or not
    }
    

}
