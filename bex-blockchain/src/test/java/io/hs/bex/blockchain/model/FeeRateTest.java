package io.hs.bex.blockchain.model;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;

public class FeeRateTest
{

    @Before
    public void setUp() throws Exception
    {}

    @Test
    public void test()
    {
        DecimalFormat decimalFormat = new DecimalFormat("#.######################");
        
        long lp = 123456; 
        long mp = 123456789; 
        long hp = 1234567891;
        
        FeeRate feeRate = new FeeRate(lp, mp, hp);
        
        System.out.println( "Fee LP:" + decimalFormat.format( lp ) );
        System.out.println( "Fee MP:" + feeRate.getMediumPriorityRate() );
        System.out.println( "Fee HP:" + feeRate.getHighPriorityRate() );
        
    }

}
