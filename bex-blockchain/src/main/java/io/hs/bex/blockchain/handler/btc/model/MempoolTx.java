package io.hs.bex.blockchain.handler.btc.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.hs.bex.blockchain.model.Coin;

@JsonIgnoreProperties( ignoreUnknown = true )
public class MempoolTx
{
    @JsonIgnore
    private String hash;
    
    private double size = 0; //Bytes
    
    private double fee = 0;
    
    private double feeRate = 0;
    
    private long time = System.currentTimeMillis();
    
    public MempoolTx() {}
    
    public MempoolTx( String hash, double size, double fee, long time )
    {
        this.hash = hash;
        this.size = size;
        this.time = time;
        setFee( fee );
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash( String hash )
    {
        this.hash = hash;
    }

    public double getSize()
    {
        return size;
    }

    public void setSize( double size )
    {
        this.feeRate = (double) (fee * Coin.SATOSHI_RATE )/size;
        this.size = size;
    }

    public double getFee()
    {
        return fee;
    }
    
    public void setFeeRate( double feeRate )
    {
        this.feeRate = feeRate;
    }

    public double getFeeRate()
    {
       return feeRate;
    }

    public void setFee( double fee )
    {
        this.feeRate = (double) (fee * Coin.SATOSHI_RATE )/size;
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
