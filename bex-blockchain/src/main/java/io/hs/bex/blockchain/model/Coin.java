package io.hs.bex.blockchain.model;

public class Coin
{
    public static final long SATOSHI_RATE = 100000000; // BTC/SATOSHI
    
    public static double getAsBtc(double value)
    {
        return (double)value/SATOSHI_RATE;
    }

}
