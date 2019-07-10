package io.hs.bex.currency.model.stats;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( Include.NON_NULL )
public class StatsRates
{
    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("scale_unit")
    private String scaleUnit;

    @JsonProperty("scale")
    private long scale;


    @JsonProperty("rates")
    private List<String> rates =  new ArrayList<>();
    
    public StatsRates(){}
    
    public StatsRates( long timeFrom, StatsType statsType )
    {
        this.timestamp = timeFrom;
        this.scale = statsType.getScale();
        this.scaleUnit = statsType.getScaleUnit().name();
    }

    @JsonProperty("time_str")
    public String getTimestampStr()
    {
        return Instant.ofEpochSecond( timestamp ) .toString();
    }


    public List<String> getRates()
    {
        return rates;
    }

    public void setRates( List<String> rates )
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

    public String getScaleUnit()
    {
        return scaleUnit;
    }

    public void setScaleUnit(String scaleUnit)
    {
        this.scaleUnit = scaleUnit;
    }

    public long getScale()
    {
        return scale;
    }

    public void setScale(long scale)
    {
        this.scale = scale;
    }
}
