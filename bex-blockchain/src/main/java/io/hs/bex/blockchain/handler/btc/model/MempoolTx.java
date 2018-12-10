package io.hs.bex.blockchain.handler.btc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.hs.bex.blockchain.model.Coin;

@JsonIgnoreProperties( ignoreUnknown = true )
public class MempoolTx
{
    @JsonIgnore
    private String hash;
    
    private long size = 0;
    
    private double fee = 0;
    
    private long time = System.currentTimeMillis();
    
    public MempoolTx() {}
    
    public MempoolTx( String hash, long size, double fee, long time )
    {
        this.hash = hash;
        this.size = size;
        this.fee = fee;
        this.time = time;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash( String hash )
    {
        this.hash = hash;
    }

    public long getSize()
    {
        return size;
    }
    
    public long getSizeAsKbytes()
    {
        return size/1024;
    }

    public void setSize( long size )
    {
        this.size = size;
    }

    public double getFee()
    {
        return fee;
    }
    
    public double getFeeRate()
    {
       return (double) ((double)fee * Coin.SATOSHI_RATE )/size;
    }

    public long getFeeRateAsLong()
    {
       return (long) ( (fee * Coin.SATOSHI_RATE )/size);
    }

    public void setFee( double fee )
    {
        this.fee = fee;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime( long time )
    {
        this.time = time;
    }
    
}
