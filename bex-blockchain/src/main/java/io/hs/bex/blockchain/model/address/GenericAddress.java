package io.hs.bex.blockchain.model.address;

import java.util.HashSet;
import java.util.Set;

import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.common.model.DigitalCurrency;
import io.hs.bex.common.model.DigitalCurrencyType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class GenericAddress
{
    private String value;
    
    private DigitalCurrency balance;
    
    private Set<GenericBlock> blocks = new HashSet<GenericBlock>();
    
    private AddressType type;
    
    public GenericAddress( DigitalCurrencyType currencyType, String value )
    {
        this.value = value;
        this.balance = new DigitalCurrency(0, currencyType);
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

   
    public Set<GenericBlock> getBlocks()
    {
        return blocks;
    }
    
    public GenericBlock addBlock( String blockHash )
    {
        GenericBlock block = blocks.stream().filter( c-> c.getHash().equals( blockHash ))
            .findFirst().orElse( new GenericBlock( balance.getType() ));
        
        blocks.add( block );
        
        return block;
    }
    
    public GenericBlock addBlock( GenericBlock block )
    {
        blocks.add( block );
        
        return block;
    }

    public void setBlocks( Set<GenericBlock> blocks )
    {
        this.blocks = blocks;
    }

    public DigitalCurrency getBalance()
    {
        return balance;
    }

    public void setBalance( DigitalCurrency balance )
    {
        this.balance = balance;
    }

    public AddressType getType()
    {
        return type;
    }

    public void setType( AddressType type )
    {
        this.type = type;
    }
    
}
