package io.hs.bex.blockchain.model.store;

public class BlockStoreData
{
    private String baseBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
    private long baseBlockHeight = 0;
    
    private String prevBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
    private long prevBlockHeight = 0;
    
    public String getBaseBlockHash()
    {
        return baseBlockHash;
    }
    
    public void setBaseBlockHash( String baseBlockHash )
    {
        this.baseBlockHash = baseBlockHash;
    }
    
    public long getBaseBlockHeight()
    {
        return baseBlockHeight;
    }
    
    public void setBaseBlockHeight( long baseBlockHeight )
    {
        this.baseBlockHeight = baseBlockHeight;
    }

    public String getPrevBlockHash()
    {
        return prevBlockHash;
    }

    public void setPrevBlockHash( String prevBlockHash )
    {
        this.prevBlockHash = prevBlockHash;
    }

    public long getPrevBlockHeight()
    {
        return prevBlockHeight;
    }

    public void setPrevBlockHeight( long prevBlockHeight )
    {
        this.prevBlockHeight = prevBlockHeight;
    }
    
    
    
}
