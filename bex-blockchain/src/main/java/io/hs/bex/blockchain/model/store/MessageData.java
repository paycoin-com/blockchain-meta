package io.hs.bex.blockchain.model.store;

public abstract class MessageData
{
    private String hash;

    public String getHash()
    {
        return hash;
    }

    public void setHash( String hash )
    {
        this.hash = hash;
    }
    
}
