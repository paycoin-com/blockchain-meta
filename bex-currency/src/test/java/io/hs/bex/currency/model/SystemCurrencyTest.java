package io.hs.bex.currency.model;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;


public class SystemCurrencyTest
{

    @Before
    public void setUp() throws Exception
    {}

    @Test
    public void test()
    {
        SysCurrency sysCurrency1 = SysCurrency.USD;
        SysCurrency sysCurrency2 = SysCurrency.BTC;
        
        assertNotEquals( sysCurrency1.getType(), sysCurrency2.getType() );
        assertNotEquals( sysCurrency1.getCode(), sysCurrency2.getCode() );
        
    }
}
