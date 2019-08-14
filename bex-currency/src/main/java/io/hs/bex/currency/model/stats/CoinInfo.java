package io.hs.bex.currency.model.stats;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.hs.bex.currency.model.SysCurrency;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class CoinInfo
{
    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("coins")
    private Map<String, Map<String,String>> coinInfoMap =  new HashMap<>();

    public CoinInfo()
    {
        timestamp = Instant.EPOCH.getEpochSecond();
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public Map<String, Map<String, String>> getCoinInfoMap()
    {
        return coinInfoMap;
    }

    public void setCoinInfoMap(Map<String, Map<String, String>> coinInfoMap)
    {
        this.coinInfoMap = coinInfoMap;
    }

    @Override public String toString()
    {
        return "CoinInfo{" + "timestamp=" + timestamp + ", coinInfoMap=" + coinInfoMap + '}';
    }
}
