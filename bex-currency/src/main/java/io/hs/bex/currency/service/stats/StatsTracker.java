package io.hs.bex.currency.service.stats;


import io.hs.bex.currency.model.stats.StatsType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//==========================
class TrackerData
{
    private StatsType statsType;
    private Instant timeStamp;

    public TrackerData(StatsType statsType, Instant timeStamp)
    {
        this.statsType = statsType;
        this.timeStamp = timeStamp;
    }

    public StatsType getStatsType()
    {
        return statsType;
    }

    public void setStatsType(StatsType statsType)
    {
        this.statsType = statsType;
    }

    public Instant getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp)
    {
        this.timeStamp = timeStamp;
    }
}
//==========================

@Component
public class StatsTracker
{
    private List<TrackerData> trackerDataList = new ArrayList<>();
    private List<StatsType> statsTypeList = new ArrayList<>();

    public StatsTracker()
    {
    }

    @PostConstruct
    public void init()
    {
        Instant tempTime = Instant.now().minus( 40, ChronoUnit.MINUTES );

        for ( StatsType statsType : StatsType.values() )
        {
            trackerDataList.add( new TrackerData( statsType, tempTime ) );
        }
    }

    public List<StatsType> requiredUpdates(Instant timestamp)
    {

        try
        {
            statsTypeList.clear();

            for ( TrackerData trackerData : trackerDataList )
            {
                if ( isUpdateRequired( trackerData.getTimeStamp(), timestamp, trackerData.getStatsType() ) )
                {
                    statsTypeList.add( trackerData.getStatsType() );
                    trackerData.setTimeStamp( timestamp );
                }
            }

            return statsTypeList;
        }
        catch ( Exception e )
        {
            System.out.println( e );
        }

        return Collections.emptyList();
    }

    private boolean isUpdateRequired(Instant startTime, Instant endTime, StatsType statsType)
    {
        long diff = statsType.getScaleUnit().between( startTime, endTime );

        if ( diff >= statsType.getScale() )
            return true;

        return false;
    }

}
