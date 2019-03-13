package io.hs.bex.blockchain.handler.btc.model;

import java.time.Instant;

public class BlockFeeData
{
    Instant time;
    
    // Satoshi
    long fee = 0;
    
    
    public BlockFeeData() {}
    
    public BlockFeeData( long timeEpochSec ) 
    {
        time = Instant.ofEpochSecond( timeEpochSec );
    }

    public Instant getTime()
    {
        return time;
    }

    public void setTime( Instant time )
    {
        this.time = time;
    }

    public long getFee()
    {
        return fee;
    }

    public void setFee( long fee )
    {
        this.fee = fee;
    }

    @Override
    public String toString()
    {
        return "BlockFeeData [time=" + time + ", fee=" + fee + "]";
    }
    
}
