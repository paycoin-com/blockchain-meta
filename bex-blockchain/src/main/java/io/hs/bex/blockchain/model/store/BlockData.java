package io.hs.bex.blockchain.model.store;

import java.util.HashSet;
import java.util.Set;

public class BlockData extends MessageData
{
    private String merkleRoot;
    private long nonce = 0;
    private long height = 0;
    private long timeSeconds = 0;
    
    Set<String> transactions = new HashSet<>();
    
    public String getMerkleRoot()
    {
        return merkleRoot;
    }
    public void setMerkleRoot( String merkleRoot )
    {
        this.merkleRoot = merkleRoot;
    }
    public long getNonce()
    {
        return nonce;
    }
    public void setNonce( long nonce )
    {
        this.nonce = nonce;
    }
    public long getHeight()
    {
        return height;
    }
    public void setHeight( long height )
    {
        this.height = height;
    }
    public long getTimeSeconds()
    {
        return timeSeconds;
    }
    public void setTimeSeconds( long timeSeconds )
    {
        this.timeSeconds = timeSeconds;
    }
    public Set<String> getTransactions()
    {
        return transactions;
    }
    public void setTransactions( Set<String> transactions )
    {
        this.transactions = transactions;
    }
    
}
