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
    @JsonProperty("stats")
    private Map<String, StatsData> statsDatas;

    @JsonGetter("volume24h")
    public String getVolume24hStr()
    {
        return  Long.toString( volume24h );
    }
    @JsonSetter("volume24h")
    public void setVolume24hStr(String volume24hStr)
    {
        this.volume24h = Long.parseLong( volume24hStr );
    }

    @JsonGetter("circulating_supply")
    public String getCirculatingSupplyStr()
    {
        return Long.toString ( circulatingSupply );
    }
    @JsonSetter("circulating_supply")
    public void setCirculatingSupply( String value )
    {
        if( !Strings.isNullOrEmpty(value))
            circulatingSupply = Long.parseLong( value );
    }

    @JsonGetter("market_cap")
    public String getMarketCapStr()
    {
        return StringUtils.doubleToString((double) circulatingSupply * latestRate );
    }
    @JsonSetter("market_cap")
    public void setMarketCap( String value )
    {
        if( !Strings.isNullOrEmpty(value))
            latestRate = (double) Double.parseDouble ( value )/circulatingSupply;
    }

    public Stats(){}

    public Stats( long epochSeconds, CoinInfo coinInfo )
    {
        statsDatas = new HashMap<String,StatsData>();

        this.circulatingSupply = coinInfo.getCirculatingSupply();
        this.volume24h = coinInfo.getVolume24h();

        Arrays.stream( StatsType.values() ).forEach( type -> statsDatas.put( type.name(),
                new StatsData( epochSeconds, type )));
    }

    @JsonIgnore
    private double latestRate = 0;

    @JsonIgnore
    private long volume24h;

    @JsonIgnore
    private long circulatingSupply = 0;


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

    public long getVolume24h()
    {
        return volume24h;
    }

    public void setVolume24h(long volume24h)
    {
        this.volume24h = volume24h;
    }
}
