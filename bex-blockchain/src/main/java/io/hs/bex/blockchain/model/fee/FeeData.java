package io.hs.bex.blockchain.model.fee;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeData
{
    @JsonProperty( "time" )
    public Long getTimeAsEpoch()
    {
        if( time == null )
            return null;

        return time.getEpochSecond();
    }

    @JsonProperty( "time_str" )
    public String getTimeAsStr()
    {
        if( time == null )
            return null;

        return time.toString();
    }

    @JsonProperty("feerates")
    private Map<String, Map<String,FeePriorityData>> coins = new HashMap<>();

    @JsonIgnore
    private Instant time = Instant.now();

    public FeeData(){}

    public void addCoin( String coinCode,long lowDuration, long medDuration, long highDuration  )
    {
        Map<String, FeePriorityData> priorityDataMap = new HashMap<>();

        priorityDataMap.put( FeePriorityType.LOW.getDisplayName(), new FeePriorityData( lowDuration ) );
        priorityDataMap.put( FeePriorityType.MEDIUM.getDisplayName(), new FeePriorityData( medDuration ) );
        priorityDataMap.put( FeePriorityType.HIGH.getDisplayName(), new FeePriorityData( highDuration ) );

        coins.put( coinCode, priorityDataMap );
    }

    public void setPriorityData( String coinCode, FeePriorityType priorityType, long rate )
    {
        Map<String, FeePriorityData> priorityDataMap = coins.get( coinCode );

        if(priorityDataMap != null && !priorityDataMap.isEmpty())
        {
            FeePriorityData priorityData = priorityDataMap.get( priorityType.getDisplayName() );

            if(priorityData != null )
                priorityData.setRate( rate );
            else
                priorityDataMap.put( priorityType.getDisplayName(),
                        new FeePriorityData( priorityType.getDefaultDuration() , rate ) );
        }
    }

    public Instant getTime()
    {
        return time;
    }

    public void setTime( Instant time )
    {
        this.time = time;
    }

    public Map<String, Map<String, FeePriorityData>> getCoins()
    {
        return coins;
    }

    public void setCoins(Map<String, Map<String, FeePriorityData>> coins)
    {
        this.coins = coins;
    }
}
