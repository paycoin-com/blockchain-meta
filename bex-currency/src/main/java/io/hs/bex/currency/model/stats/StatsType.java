package io.hs.bex.currency.model.stats;


import java.time.Instant;
import java.time.temporal.ChronoUnit;


public enum StatsType
{
    DAILY( ChronoUnit.MINUTES, 30, 48),
    WEEKLY( ChronoUnit.HOURS, 3, 56 ),
    MONTHLY( ChronoUnit.HOURS, 12, 60),
    MONTHLY6( ChronoUnit.DAYS, 3, 60),
    ANNUAL( ChronoUnit.DAYS, 7, 52 );
    
    private ChronoUnit scaleUnit;
    private short scale;
    private short recordCount;


    StatsType( ChronoUnit ChronoUnit, int timeRange, int recordCount )
    {
        this.scaleUnit = ChronoUnit;
        this.scale = (short) timeRange;
        this.recordCount = (short) recordCount;
    }

    public ChronoUnit getScaleUnit()
    {
        return scaleUnit;
    }

    public short getRecordCount()
    {
        return recordCount;
    }

    public short getScale()
    {
        return scale;
    }


}
