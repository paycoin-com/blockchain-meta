package io.hs.bex.blocknode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class NodeNetwork
{
    
    private NodeNetworkType type = NodeNetworkType.TESTNET;
    private String host;
    
    public NodeNetwork( NodeNetworkType networkType )
    {
        this.type = networkType; 
    }
    
    public NodeNetworkType getType()
    {
        return type;
    }
    public void setType( NodeNetworkType type )
    {
        this.type = type;
    }
    public String getHost()
    {
        return host;
    }
    public void setHost( String host )
    {
        this.host = host;
    }

}
