package io.hs.bex.blockchain.handler.btc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class MempoolInfo
{
    private long size = 0;
    
    private long time = 0;
    
    private long blockCreateTime = 0;
    
    public long getSize()
    {
        return size;
    }

    public void setSize( long size )
    {
        this.size = size;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime( long time )
    {
        this.time = time;
    }

    public long getBlockCreateTime()
    {
        return blockCreateTime;
    }

    public void setBlockCreateTime( long blockCreateTime )
    {
        this.blockCreateTime = blockCreateTime;
    }
    
   
}
