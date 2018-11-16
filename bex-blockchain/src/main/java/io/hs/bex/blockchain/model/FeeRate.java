package io.hs.bex.blockchain.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.hs.bex.common.utils.StringUtils;

@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeRate
{
    @JsonIgnore
    private Instant date = Instant.now();

    @JsonIgnore
    public double lowPriorityRate  = 0;
    
    @JsonIgnore
    public double mediumPriorityRate = 0;
    
    @JsonIgnore
    public double highPriorityRate  = 0;
    
    public FeeRate() {}
    
    public FeeRate( double lowPriority, double mediumPriority, double highPriority )
    {
        this.lowPriorityRate = lowPriority;
        this.mediumPriorityRate = mediumPriority;
        this.highPriorityRate = highPriority;
    }

    @JsonProperty("low_priority")
    public String getLowPriorityStr()
    {
        return StringUtils.doubleToString( lowPriorityRate );
    }

    @JsonProperty("medium_priority")
    public String getMediumPriorityStr()
    {
        return StringUtils.doubleToString( mediumPriorityRate );
    }
    
    @JsonProperty("high_priority")
    public String getHighPriorityStr()
    {
        return StringUtils.doubleToString( highPriorityRate );
    }
    
    @JsonIgnore
    public Instant getDate()
    {
        return date;
    }

    public void setDate( Instant date )
    {
        this.date = date;
    }

    @JsonProperty("date_str")
    public String getDateStr()
    {
        return StringUtils.instantToString( date ); 
    }
    
    @JsonProperty("date")
    public long getDateEpoch()
    {
        return date.toEpochMilli();
    }


}