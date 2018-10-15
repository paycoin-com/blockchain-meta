package io.hs.bex.blockchain.model.store;

public class BlockDataLimited  extends MessageData
{
    private long height = 0;
    
    public BlockDataLimited() {}

    public BlockDataLimited( String hash, long height )
    {
        this.height = height;
        this.setHash( hash );
    }

    public long getHeight()
    {
        return height;
    }

    public void setHeight( long height )
    {
        this.height = height;
    }
    
}
