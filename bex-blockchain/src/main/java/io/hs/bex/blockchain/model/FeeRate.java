package io.hs.bex.blockchain.model;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.hs.bex.common.utils.StringUtils;


/**
 * Data container for Fee rates based on Coin/Bytes (Satoshi for BTC)
 * 
 * @author nisakov
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeRate
{
    @JsonIgnore
    private Instant date = Instant.now();

    @JsonIgnore
    public double lowPriorityRate = 0;

    @JsonIgnore
    public double mediumPriorityRate = 0;

    @JsonIgnore
    public double highPriorityRate = 0;

    public FeeRate()
    {}

    public FeeRate( double lowPriority, double mediumPriority, double highPriority )
    {
        this.lowPriorityRate = lowPriority;
        this.mediumPriorityRate = mediumPriority;
        this.highPriorityRate = highPriority;
    }

    @JsonProperty( "low_priority" )
    public String getLowPriorityStr()
    {
        return StringUtils.doubleToString( lowPriorityRate );
    }

    @JsonProperty( "medium_priority" )
    public String getMediumPriorityStr()
    {
        return StringUtils.doubleToString( mediumPriorityRate );
    }

    @JsonProperty( "high_priority" )
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

    @JsonIgnore
    public double getLowPriorityRate()
    {
        return lowPriorityRate;
    }

    @JsonIgnore
    public double getMediumPriorityRate()
    {
        return mediumPriorityRate;
    }

    @JsonIgnore
    public double getHighPriorityRate()
    {
        return highPriorityRate;
    }

    @JsonProperty( "date_str" )
    public String getDateStr()
    {
        return StringUtils.instantToString( date );
    }

    @JsonProperty( "date" )
    public long getDateEpoch()
    {
        return date.toEpochMilli();
    }

    @JsonIgnore
    public FeeRate convertToCoinKbytes( long coinRate )
    {
        return new FeeRate( ( lowPriorityRate / coinRate) * 1024, ( mediumPriorityRate / coinRate) * 1024,
                ( highPriorityRate / coinRate) * 1024 );
    }

    @Override
    public String toString()
    {
        return "FeeRate [date=" + date + ", lowPriorityRate=" + lowPriorityRate + ", mediumPriorityRate="
                + mediumPriorityRate + ", highPriorityRate=" + highPriorityRate + "]";
    }

}
