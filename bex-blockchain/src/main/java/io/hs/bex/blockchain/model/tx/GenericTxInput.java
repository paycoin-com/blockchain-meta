package io.hs.bex.blockchain.model.tx;

import io.hs.bex.blockchain.model.address.GenericAddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class GenericTxInput extends AbstractIOTransaction
{
    @JsonProperty("coinbase")
    private boolean coinBase = false;
    
    @JsonProperty("to_address")
    private GenericAddress fromAddress;
    
    public GenericTxInput( GenericTransaction transaction )
    {
        super( transaction,TransactionIOType.INPUT );
        
        transaction.getTxInputs().add( this );
    }
    
    public boolean isCoinBase()
    {
        return coinBase;
    }

    public void setCoinBase( boolean coinBase )
    {
        this.coinBase = coinBase;
    }

    public GenericAddress getFromAddress()
    {
        return fromAddress;
    }

    public void setFromAddress( GenericAddress fromAddress )
    {
        this.fromAddress = fromAddress;
    }
    
    public void setFromAddress( String addressValue )
    {
        if(this.fromAddress == null)
            this.fromAddress = new GenericAddress(getParent().getCurrencyType(), addressValue );
    }
    
}
