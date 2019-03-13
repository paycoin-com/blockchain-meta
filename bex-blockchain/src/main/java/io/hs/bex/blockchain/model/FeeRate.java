package io.hs.bex.blockchain.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


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
    public long lowPriorityRate = 0;

    @JsonIgnore
    public long mediumPriorityRate = 0;

    @JsonIgnore
    public long highPriorityRate = 0;

    public FeeRate()
    {}

    public FeeRate( double lowPriority, double mediumPriority, double highPriority )
    {
        this.lowPriorityRate = (long) ( ( lowPriority * Coin.SATOSHI_RATE) / 1024);
        this.mediumPriorityRate = (long) ( mediumPriority * Coin.SATOSHI_RATE) / 1024;;
        this.highPriorityRate = (long) ( highPriority * Coin.SATOSHI_RATE) / 1024;;
    }
    
    public FeeRate( long lowPriority, long mediumPriority, long highPriority )
    {
        this.lowPriorityRate = lowPriority;
        this.mediumPriorityRate = mediumPriority;
        this.highPriorityRate = highPriority;
    }

    @JsonProperty( "low_priority" )
    public long getLowPriorityRate()
    {
        return lowPriorityRate;
    }

    @JsonProperty( "medium_priority" )
    public long getMediumPriorityRate()
    {
        return mediumPriorityRate;
    }

    @JsonProperty( "high_priority" )
    public long getHighPriorityRate()
    {
        return highPriorityRate;
    }
    
    public void setLowPriorityRate( long lowPriorityRate )
    {
        this.lowPriorityRate = lowPriorityRate;
    }

    public void setMediumPriorityRate( long mediumPriorityRate )
    {
        this.mediumPriorityRate = mediumPriorityRate;
    }

    public void setHighPriorityRate( long highPriorityRate )
    {
        this.highPriorityRate = highPriorityRate;
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
        return "FeeRate [ lowPriorityRate=" + lowPriorityRate + ", mediumPriorityRate="
                + mediumPriorityRate + ", highPriorityRate=" + highPriorityRate + "]";
    }

}
