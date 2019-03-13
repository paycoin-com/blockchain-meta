package io.hs.bex.blockchain.handler.btc.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class BlockInfo
{
    @JsonProperty("height")
    private int height;
    
    @JsonProperty("time")
    private long time;
    
    @JsonProperty("txs")
    private List<TxInfo> txs;

    public int getHeight()
    {
        return height;
    }

    public void setHeight( int height )
    {
        this.height = height;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime( long time )
    {
        this.time = time;
    }

    public List<TxInfo> getTxs()
    {
        return txs;
    }

    public void setTxs( List<TxInfo> txs )
    {
        this.txs = txs;
    }
}
