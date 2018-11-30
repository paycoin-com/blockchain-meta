package io.hs.bex.currency.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CurrencyInfoRequestTest
{

    @Before
    public void setUp() throws Exception
    {}

    @Test
    public void testJoin()
    {
        SysCurrency sourceCurrency = SysCurrency.find( "BTC" );
        SysCurrency targetCurrency1 = SysCurrency.find( "USD" );
        SysCurrency targetCurrency2 = SysCurrency.find( "EUR" );
        SysCurrency targetCurrency3 = SysCurrency.find( "RUB" );
        SysCurrency targetCurrency4 = SysCurrency.find( "JPY" );
        
        CurrencyInfoRequest infoRequest =  new CurrencyInfoRequest( sourceCurrency, targetCurrency1 );
        
        infoRequest.getTargetCurrencies().add( targetCurrency2 );
        infoRequest.getTargetCurrencies().add( targetCurrency3 );
        infoRequest.getTargetCurrencies().add( targetCurrency4 );
        
        String joined = infoRequest.joinTargetCurrencies( "," ) ;
        
        System.out.println( "Joined:" + joined );
        
        assertEquals( "USD,EUR,RUB,JPY", joined );
    }

}
