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
        if(!containsBlock( hash ))
            blocks.add( new BlockDataLimited( hash, height ) );
    }
    
    public void addBlockData( BlockDataLimited block ) 
    {
        if(!containsBlock( block.getHash() ))
            blocks.add( new BlockDataLimited( block.getHash(), block.getHeight() ) );
    }

    
    public boolean containsBlock( String hash ) 
    {
        return blocks.stream().anyMatch( item -> item.getHash().equals( hash ));
    }
    
    public void addBlocks( Set<BlockDataLimited> inpBlocks ) 
    {
        for(BlockDataLimited block : inpBlocks) 
        {
            addBlockData(block);
        }
    }
    
}
