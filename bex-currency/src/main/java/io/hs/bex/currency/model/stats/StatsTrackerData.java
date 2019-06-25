package io.hs.bex.currency.model.stats;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class StatsTrackerData
{   
    private Instant timestamp;
    
    private int lastHour = 0;
    private int lastDay = 0;
    private int lastMonth = 0;
    
    public StatsTrackerData() {}
    
    public StatsTrackerData( Instant timestamp )
    {
        LocalDateTime ldt = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        
        this.timestamp = timestamp;
        this.lastHour = ldt.getHour();
        this.lastDay = ldt.getDayOfMonth();
        this.lastMonth = ldt.getMonthValue();
    }

    public static StatsTrackerData getTrackerData( Instant dt ) 
    {
        return new StatsTrackerData( dt );
    }
    
    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Instant timestamp )
    {
        this.timestamp = timestamp;
    }

    public int getLastDay()
    {
        return lastDay;
    }
    
    public void setLastDay( int lastDay )
    {
        this.lastDay = lastDay;
    }
    
    public int getLastMonth()
    {
        return lastMonth;
    }
    
    public void setLastMonth( int lastMonth )
    {
        this.lastMonth = lastMonth;
    }
    
    public int getLastHour()
    {
        return lastHour;
    }
    
    public void setLastHour( int lastHour )
    {
        this.lastHour = lastHour;
    }
    
    public void updateData( Instant dt ) 
    {
        LocalDateTime ldt = LocalDateTime.ofInstant(dt, ZoneId.systemDefault());
        setLastHour( ldt.getHour() );
        setLastDay( ldt.getDayOfMonth() );
        setLastMonth( ldt.getMonthValue() );
    }
    
}
