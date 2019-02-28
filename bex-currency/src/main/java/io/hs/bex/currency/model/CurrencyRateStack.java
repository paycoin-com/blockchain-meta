package io.hs.bex.currency.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( Include.NON_NULL )
public class CurrencyRateStack
{
    @JsonIgnore
    private SysCurrency currency;
    
    @JsonIgnore
    private Instant time;
    
    private Map<SysCurrency, Float> rates = new LinkedHashMap<SysCurrency, Float>();

    public SysCurrency getCurrency()
    {
        return currency;
    }
    
    public void setCurrency( SysCurrency currency )
    {
        this.currency = currency;
    }
    
    public Instant getTime()
    {
        return time;
    }
    
    @JsonProperty("currency")
    public String getCurrencyStr()
    {
        return currency.getCode();
    }
    
    @JsonProperty("time")
    public long getTimeAsEpoch()
    {
        return time.toEpochMilli();
    }

    @JsonProperty("time_str")
    public String getTimeAsStr()
    {
        return time.toString();
    }

    public void setTime( Instant time )
    {
        this.time = time;
    }

    public Map<SysCurrency, Float> getRates()
    {
        return rates;
    }

    public void setRates( Map<SysCurrency, Float> rates )
    {
        this.rates = rates;
    }
    
}
