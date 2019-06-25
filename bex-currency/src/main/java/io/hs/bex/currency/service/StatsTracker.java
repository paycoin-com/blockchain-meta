package io.hs.bex.currency.service;


import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.hs.bex.currency.model.stats.Stats;
import io.hs.bex.currency.model.stats.StatsTrackerData;


@Component
public class StatsTracker
{
    private List<Stats> statsList = new ArrayList<Stats>();

    private StatsTrackerData trackerData;

    @PostConstruct
    private void init()
    {
        Instant timestamp = ZonedDateTime.now(ZoneOffset.UTC).minusMonths(1).toInstant();
        
        timestamp = timestamp.minus( 1, ChronoUnit.HOURS );
        timestamp = timestamp.minus( 1, ChronoUnit.DAYS );

        trackerData = StatsTrackerData.getTrackerData( timestamp );
    }

    public List<Stats> getStatsList()
    {
        return statsList;
    }

    public void setStatsList( List<Stats> statsList )
    {
        this.statsList = statsList;
    }

    public StatsTrackerData getTrackerData()
    {
        return trackerData;
    }

    public void setTrackerData( StatsTrackerData trackerData )
    {
        this.trackerData = trackerData;
    }

    public void updateTrackerData( Instant dt )
    {
        trackerData.updateData( dt );
    }

    public void updateTrackerData( StatsTrackerData newTrackerData )
    {
        this.trackerData = newTrackerData;
    }

}