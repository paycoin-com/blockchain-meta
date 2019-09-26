package io.hs.bex.currency.model.stats;


import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;


public class StatsTypeTest
{
    @Before
    public void setUp() throws Exception
    {}

    @Test
    public void testAdjustTime()
    {
        LocalDateTime ldt = LocalDateTime.now();

        System.out.println( "Current Time--:" + ldt.toString() );

        for(int x=0;x<60;x++)
            ldt = adjustDateTime(StatsType.MONTHLY, ldt);

        System.out.println( "After 1 Adjust:" + ldt.toString() );
    }

    private LocalDateTime adjustDateTime(StatsType statsType, LocalDateTime dateTime)
    {
        return dateTime.minus( statsType.getScale(), statsType.getScaleUnit() );
    }

}
