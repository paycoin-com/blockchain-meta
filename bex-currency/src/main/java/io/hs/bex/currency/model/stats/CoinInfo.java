package io.hs.bex.currency.model.stats;


import io.hs.bex.currency.model.SysCurrency;

public class CoinInfo
{
    private SysCurrency coin;
    private long circulatingSupply = 0;
    private long volume24h = 0;


    public CoinInfo( String coinStr, long circulatingSupply, long volume24h )
    {
        this.coin = SysCurrency.find( coinStr );
        this.circulatingSupply = circulatingSupply;
        this.volume24h = volume24h;
    }
    public CoinInfo( SysCurrency coin, long circulatingSupply, long volume24h )
    {
        this.coin = coin;
        this.circulatingSupply = circulatingSupply;
        this.volume24h = volume24h;
    }

    public SysCurrency getCoin()
    {
        return coin;
    }

    public void setCoin(SysCurrency coin)
    {
        this.coin = coin;
    }

    public long getCirculatingSupply()
    {
        return circulatingSupply;
    }

    public void setCirculatingSupply(long circulatingSupply)
    {
        this.circulatingSupply = circulatingSupply;
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
