package io.hs.bex.currency.model.stats;

import java.time.Instant;

import io.hs.bex.currency.model.SysCurrency;

public class Stats
{
    private float rate = 0;
    
    private StatsType type;
    
    private SysCurrency fiatCurrency;
    
    private SysCurrency digCurrency;
    
    private Instant timeStamp;
    
    public Stats(){}

//    public Stats( SysCurrency fiatCurrency, SysCurrency digCurrency, float rate, Instant timestamp )
//    {
//        this.fiatCurrency = fiatCurrency;
//        this.digCurrency = digCurrency;
//        this.rate = rate;
//
//        if(timestamp == null)
//            this.timeStamp = Instant.now();
//        else
//            this.timeStamp = timestamp;
//    }

    public float getRate()
    {
        return rate;
    }

    public void setRate( float rate )
    {
        this.rate = rate;
    }

    public StatsType getType()
    {
        return type;
    }

    public void setType( StatsType type )
    {
        this.type = type;
    }

    public Instant getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp( Instant timeStamp )
    {
        this.timeStamp = timeStamp;
    }

    public SysCurrency getFiatCurrency()
    {
        return fiatCurrency;
    }

    public void setFiatCurrency( SysCurrency fiatCurrency )
    {
        this.fiatCurrency = fiatCurrency;
    }

    public SysCurrency getDigCurrency()
    {
        return digCurrency;
    }

    public void setDigCurrency( SysCurrency digCurrency )
    {
        this.digCurrency = digCurrency;
    }
    
}
