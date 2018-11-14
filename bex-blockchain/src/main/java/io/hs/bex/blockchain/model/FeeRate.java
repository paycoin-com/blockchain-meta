package io.hs.bex.blockchain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeRate
{
    @JsonProperty("fastestFee")
    public double fastestFee = 0;

    @JsonProperty("halfHourFee")
    public double halfHourFee = 0;
    
    @JsonProperty("hourFee")
    public double hourFee = 0;

    public double getFastestFee()
    {
        return fastestFee;
    }

    public void setFastestFee( double fastestFee )
    {
        this.fastestFee = fastestFee;
    }

    public double getHalfHourFee()
    {
        return halfHourFee;
    }

    public void setHalfHourFee( double halfHourFee )
    {
        this.halfHourFee = halfHourFee;
    }

    public double getHourFee()
    {
        return hourFee;
    }

    public void setHourFee( double hourFee )
    {
        this.hourFee = hourFee;
    }
    
}
