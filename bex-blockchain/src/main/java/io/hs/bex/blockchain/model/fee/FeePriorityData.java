package io.hs.bex.blockchain.model.fee;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.temporal.ChronoUnit;


@JsonIgnoreProperties( ignoreUnknown = true )
public class FeePriorityData
{

    @JsonProperty("rate")
    private long rate = 0;

    @JsonProperty("duration")
    private long duration = 0;

    @JsonProperty("duration_unit")
    public String getDurationUnitStr()
    {
        return durationUnit.name();
    }

    @JsonIgnore
    private FeePriorityType feeRatePriority;

    @JsonIgnore
    private ChronoUnit durationUnit = ChronoUnit.MINUTES;

    public FeePriorityData(){}

    public FeePriorityData( long duration )
    {
        this.duration = duration;
    }

    public FeePriorityData( long duration, long rate )
    {
        this.duration = duration;
        this.rate = rate;
    }

    public long getRate()
    {
        return rate;
    }

    public void setRate(long rate)
    {
        this.rate = rate;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public FeePriorityType getFeeRatePriority()
    {
        return feeRatePriority;
    }

    public void setFeeRatePriority(FeePriorityType feeRatePriority)
    {
        this.feeRatePriority = feeRatePriority;
    }

    public ChronoUnit getDurationUnit()
    {
        return durationUnit;
    }

    public void setDurationUnit(ChronoUnit durationUnit)
    {
        this.durationUnit = durationUnit;
    }
}
