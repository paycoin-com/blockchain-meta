package io.hs.bex.blockchain.model;


import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hs.bex.common.model.DigitalCurrencyType;


@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeRateStack
{
    @JsonIgnore
    private Instant time = Instant.now();

    private Map<DigitalCurrencyType, FeeRate> rates = Collections
            .synchronizedMap( ( new LinkedHashMap<DigitalCurrencyType, FeeRate>()) );

    public Instant getTime()
    {
        return time;
    }

    public void setTime( Instant time )
    {
        this.time = time;
    }

    public Map<DigitalCurrencyType, FeeRate> getRates()
    {
        return rates;
    }

    public void setRates( Map<DigitalCurrencyType, FeeRate> rates )
    {
        this.rates = rates;
    }

    @JsonProperty( "time" )
    public Long getTimeAsEpoch()
    {
        if( time == null )
            return null;

        return time.toEpochMilli();
    }

    @JsonProperty( "time_str" )
    public String getTimeAsStr()
    {
        if( time == null )
            return null;

        return time.toString();
    }
}
