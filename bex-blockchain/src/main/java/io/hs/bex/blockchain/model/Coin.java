package io.hs.bex.blockchain.model;

public class Coin
{
    public static final long SATOSHI_RATE = 100000000; // BTC/SATOSHI
    
    public static final long GWEI_RATE = 1000000000; // BTC/SATOSHI

    
    public static double getAsBtc(double value)
    {
        return (double)value/SATOSHI_RATE;
    }
    
    
    public static int getAsGwei( long wei_value )
    {
        return (int) Math.ceil( (double)wei_value/GWEI_RATE );
    }


}
