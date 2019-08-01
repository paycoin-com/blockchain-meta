package io.hs.bex.currency.service.stats;


import io.hs.bex.currency.model.stats.StatsType;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class StatsTrackerTest
{

    @Test
    public void testGetRequiredUpdates()
    {
        int DAILY_COUNT = 0;
        int WEEKLY_COUNT = 0;
        int MONTHLY_COUNT =0;

        StatsTracker statsTracker = new StatsTracker();
        statsTracker.init();

        Instant timestamp = Instant.now();

        // 420 = 7 hours

        for(int x = 30; x < 4200; x+=30 )
        {
            timestamp = timestamp.plus( 30, ChronoUnit.MINUTES );
            List<StatsType> requiredStats = statsTracker.requiredUpdates( timestamp );

            if(!requiredStats.isEmpty())
            {
                if(requiredStats.contains( StatsType.DAILY ))
                    DAILY_COUNT++;
                if(requiredStats.contains( StatsType.WEEKLY ))
                    WEEKLY_COUNT++;
                if(requiredStats.contains( StatsType.MONTHLY ))
                    MONTHLY_COUNT++;

                //System.out.println( "Timestamp:" + timestamp );
                //requiredStats.forEach(System.out::println);
            }

            //System.out.println( "------------------");
        }

        assertEquals( DAILY_COUNT , (4200 - StatsType.DAILY.getScale())/StatsType.DAILY.getScale() );
        assertEquals( WEEKLY_COUNT ,23 );
        assertEquals( MONTHLY_COUNT , 5 );

    }
}
