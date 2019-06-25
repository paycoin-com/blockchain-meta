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
    
    @JsonProperty("rates")
    private List<String> rates =  new ArrayList<>();
    
    public StatsRates(){}
    
    public StatsRates( long timestamp )
    {
        this.timestamp = timestamp;
    }

    @JsonProperty("time_str")
    public String getTimeAsStr()
    {
        return Instant.ofEpochSecond( timestamp ) .toString();
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }

    public List<String> getRates()
    {
        return rates;
    }

    public void setRates( List<String> rates )
    {
        this.rates = rates;
    }

}   
