package io.hs.bex.blockchain.model;

public class LocalStoreStatus
{
    public final static int STATUS_DATA_ID = 1;
    
    private int id = STATUS_DATA_ID;
    
    private long blockIndex = 0;
    
    private long addressIndex = 0;
    
    private long txIndex = 0;
    
    private String blockHash;
    
    
    public LocalStoreStatus()
    {
    }
    
    public LocalStoreStatus( long blockIndex, long txIndex, long addressIndex, String blockHash )
    {
        this.blockIndex = blockIndex;
        this.addressIndex = addressIndex;
        this.txIndex = txIndex;
        this.blockHash = blockHash;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    public long getBlockIndex()
    {
        return blockIndex;
    }
    
    public void setBlockIndex( long blockIndex )
    {
        this.blockIndex = blockIndex;
    }
    
    public long getAddressIndex()
    {
        return addressIndex;
    }
    
    public void setAddressIndex( long addressIndex )
    {
        this.addressIndex = addressIndex;
    }
    
    public long getTxIndex()
    {
        return txIndex;
    }
    
    public void setTxIndex( long txIndex )
    {
        this.txIndex = txIndex;
    }

    public String getBlockHash()
    {
        return blockHash;
    }

    public void setBlockHash( String blockHash )
    {
        this.blockHash = blockHash;
    }
    
    
}
