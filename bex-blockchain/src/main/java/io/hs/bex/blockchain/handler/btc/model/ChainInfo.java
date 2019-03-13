package io.hs.bex.blockchain.handler.btc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class ChainInfo
{
    @JsonProperty("height")
    private int lastBlockHeight;

    public int getLastBlockHeight()
    {
        return lastBlockHeight;
    }

    public void setLastBlockHeight( int lastBlockHeight )
    {
        this.lastBlockHeight = lastBlockHeight;
    }
    
}
