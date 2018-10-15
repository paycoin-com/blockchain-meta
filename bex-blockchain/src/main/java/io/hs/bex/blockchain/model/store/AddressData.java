package io.hs.bex.blockchain.model.store;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude( Include.NON_NULL )
public class AddressData
{
    private String address;
    
    Set<BlockDataLimited> blocks = new HashSet<>();
    
    public String getAddress()
    {
        return address;
    }
    
    public void setAddress( String address )
    {
        this.address = address;
    }
    
    public Set<BlockDataLimited> getBlocks()
    {
        return blocks;
    }
    
    public void setBlocks( Set<BlockDataLimited> blocks )
    {
        this.blocks = blocks;
    }
    
    public void addBlockData( String hash, long height ) 
    {
        blocks.add( new BlockDataLimited( hash, height ) );
    }
    
}
