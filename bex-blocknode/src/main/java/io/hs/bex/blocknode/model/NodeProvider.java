package io.hs.bex.blocknode.model;

import io.hs.bex.common.model.DigitalCurrencyType;

public class NodeProvider
{
    NodeNetworkType networkType;
    
    private DigitalCurrencyType currencyType;

    public NodeProvider( DigitalCurrencyType currencyType, NodeNetworkType networkType )
    {
        this.currencyType = currencyType;
        this.networkType = networkType;
    }
    
    public NodeNetworkType getNetworkType()
    {
        return networkType;
    }

    public void setNetworkType( NodeNetworkType networkType )
    {
        this.networkType = networkType;
    }

    public DigitalCurrencyType getCurrencyType()
    {
        return currencyType;
    }

    public int getId()
    {
        return currencyType.getId() + networkType.getId();
    }
   
}
