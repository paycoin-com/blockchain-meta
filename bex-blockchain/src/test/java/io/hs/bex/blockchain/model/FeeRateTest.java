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
        
        double lp = 0.00000000123456; 
        double mp = 0.00000000123456789; 
        double hp = 0.0000000012345678912;
        
        FeeRate feeRate = new FeeRate(lp, mp, hp);
        
        System.out.println( "Fee LP:" + decimalFormat.format( lp ) );
        System.out.println( "Fee MP:" + feeRate.getMediumPriorityStr() );
        System.out.println( "Fee HP:" + feeRate.getHighPriorityStr() );
        
    }

}
