package io.hs.bex.currency.model.stats;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( Include.NON_NULL )
public class StatsData
{
    @JsonProperty( "timestamp" )
    private long timestamp;

    @JsonProperty( "time_str" )
    public String getTimestampStr()
    {
        return Instant.ofEpochSecond( timestamp ).toString();
    }

    @JsonProperty( "scale_unit" )
    public String getScaleUnit()
    {
        return statsType.getScaleUnit().name();
    }

    @JsonProperty( "scale" )
    public long getScale()
    {
        return statsType.getScale();
    }

    @JsonProperty( "scale_minutes" )
    public long getScaleInMinutes()
    {
        return statsType.getScaleUnit().getDuration().toMinutes() * this.getScale();
    }

    @JsonProperty( "rates" )
    private List<String> rates = new ArrayList<>();

    @JsonIgnore
    private StatsType statsType;

    @JsonCreator
    public StatsData( @JsonProperty("scale_unit") String scaleUnit, @JsonProperty("scale") long scale)
    {
        ChronoUnit sUnit = ChronoUnit.valueOf( scaleUnit );
        statsType = Arrays.stream( StatsType.values() )
                .filter( type -> type.getScale() == scale && type.getScaleUnit() == sUnit ).findAny().get();
    }

    public StatsData( long timeFrom, StatsType statsType )
    {
        if ( timeFrom == 0 )
            this.timestamp = Instant.now().getEpochSecond();
        else
            this.timestamp = timeFrom;

        this.statsType = statsType;
    }

    public List<String> getRates()
    {
        return rates;
    }

    public void setRates(List<String> rates)
    {
        this.rates = rates;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public StatsType getStatsType()
    {
        return statsType;
    }

    public void setStatsType(StatsType statsType)
    {
        this.statsType = statsType;
    }
}