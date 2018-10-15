package io.hs.bex.blockchain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractMessage
{
    private long version = 0;

    private String hash;
    
    @JsonProperty("message_size")
    private int messageSize = 0;


    public String getHash()
    {
        return hash;
    }

    public void setHash( String hash )
    {
        this.hash = hash;
    }

    public long getVersion()
    {
        return version;
    }

    public void setVersion( long version )
    {
        this.version = version;
    }

    public int getMessageSize()
    {
        return messageSize;
    }

    public void setMessageSize( int messageSize )
    {
        this.messageSize = messageSize;
    }
    
    
}
