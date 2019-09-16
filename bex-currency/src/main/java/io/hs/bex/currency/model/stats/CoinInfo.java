package io.hs.bex.currency.model.stats;


import io.hs.bex.currency.model.SysCurrency;


public class CoinInfo
{
    private SysCurrency coin;
    private SysCurrency fiat;

    private long circulatingSupply = 0;
    private double volume24h = 0;
    private double rate = 0;

    public CoinInfo(String coinCode, String fiatCode)
    {
        this.coin = SysCurrency.find( coinCode );
        this.fiat = SysCurrency.find( fiatCode );

        this.circulatingSupply = 0;
        this.volume24h = 0;
        this.rate = 0;
    }

    public CoinInfo(String coinCode, String fiatCode, long circulatingSupply, double volume24h, double rate)
    {
        this.coin = SysCurrency.find( coinCode );
        this.fiat = SysCurrency.find( fiatCode );
        this.circulatingSupply = circulatingSupply;
        this.volume24h = volume24h;
        this.rate = rate;
    }

    public CoinInfo(SysCurrency coin, SysCurrency fiat, long circulatingSupply, double volume24h, double rate)
    {
        this.coin = coin;
        this.fiat = fiat;
        this.circulatingSupply = circulatingSupply;
        this.volume24h = volume24h;
        this.rate = rate;
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

    public double getVolume24h()
    {
        return volume24h;
    }

    public void setVolume24h(double volume24h)
    {
        this.volume24h = volume24h;
    }

    public double getRate()
    {
        return rate;
    }

    public void setRate(double rate)
    {
        this.rate = rate;
    }

    public SysCurrency getFiat()
    {
        return fiat;
    }

    public void setFiat(SysCurrency fiat)
    {
        this.fiat = fiat;
    }
}
