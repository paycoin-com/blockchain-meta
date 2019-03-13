package io.hs.bex.blockchain.handler.btc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class PeerInfo
{
    @JsonProperty("version")
    private String version; 

    @JsonProperty("network")
    private String network; 

    @JsonProperty("chain")
    private ChainInfo chainInfo;

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getNetwork()
    {
        return network;
    }

    public void setNetwork( String network )
    {
        this.network = network;
    }

    public ChainInfo getChainInfo()
    {
        return chainInfo;
    }

    public void setChainInfo( ChainInfo chainInfo )
    {
        this.chainInfo = chainInfo;
    }
    
    
    
}
