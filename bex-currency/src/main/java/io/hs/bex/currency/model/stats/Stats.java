package io.hs.bex.currency.model.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Strings;
import io.hs.bex.common.utils.StringUtils;


@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( Include.NON_NULL )
@JsonPropertyOrder({ "circulating_supply", "market_cap", "volume24h", "stats" })
public class Stats
{
    @JsonGetter("volume24h")
    public String getVolume24hStr()
    {
        return  StringUtils.doubleToString( volume24h );
    }
    @JsonSetter("volume24h")
    public void setVolume24hStr(String volume24hStr)
    {
        this.volume24h = Double.parseDouble( volume24hStr );
    }

    @JsonProperty("circulating_supply")
    private long circulatingSupply = 0;

    @JsonGetter("market_cap")
    public String getMarketCapStr()
    {
        if(circulatingSupply <= 0 || latestRate  <= 0)
            return "0";

        return StringUtils.doubleToString((double) circulatingSupply * latestRate );
    }
    @JsonSetter("market_cap")
    public void setMarketCap( String value )
    {
        try
        {
            if ( !Strings.isNullOrEmpty( value ) )
                latestRate = (double) Double.parseDouble( value ) / circulatingSupply;
            else
                latestRate = 0;
        }
        catch(Exception e)
        {
            latestRate = 0;
        }
    }

    @JsonProperty("stats")
    private Map<String, StatsData> statsDatas;

    public Stats(){}

    public Stats( long epochSeconds, CoinInfo coinInfo )
    {
        statsDatas = new HashMap<>();

        this.circulatingSupply = coinInfo.getCirculatingSupply();
        this.volume24h = coinInfo.getVolume24h();

        Arrays.stream( StatsType.values() ).forEach( type -> statsDatas.put( type.name(),
                new StatsData( epochSeconds, type )));
    }

    @JsonIgnore
    private double latestRate = 0;

    @JsonIgnore
    private double volume24h;


    public Map<String, StatsData> getStatsDatas()
    {
        return statsDatas;
    }

    public void setStatsDatas(Map<String, StatsData> statsDatas)
    {
        this.statsDatas = statsDatas;
    }

    public double getLatestRate()
    {
        return latestRate;
    }

    public void setLatestRate(double latestRate)
    {
        this.latestRate = latestRate;
    }

    public long getCirculatingSupply()
    {
        return circulatingSupply;
    }

    public void setCirculatingSupply(long circulatingSupply)
    {
        this.circulatingSupply = circulatingSupply;
    }

    public void setLatestRate(float latestRate)
    {
        this.latestRate = latestRate;
    }

    public double getVolume24h()
    {
        return volume24h;
    }

    public void setVolume24h(double volume24h)
    {
        this.volume24h = volume24h;
    }
}
