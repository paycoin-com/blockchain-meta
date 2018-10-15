package io.hs.bex.blockchain.model.tx;

import io.hs.bex.blockchain.model.address.GenericAddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class GenericTxOutput extends AbstractIOTransaction
{
    @JsonProperty("to_address")
    private GenericAddress toAddress;
    
    public GenericTxOutput( GenericTransaction transaction )
    {
        super( transaction,TransactionIOType.OUTPUT );
        
        transaction.getTxOutputs().add( this );
    }
    
    public void setToAddress( String addressValue )
    {
        if(this.toAddress == null)
            this.toAddress = new GenericAddress(getParent().getCurrencyType(), addressValue );
    }

    public GenericAddress getToAddress()
    {
        return toAddress;
    }

    public void setToAddress( GenericAddress toAddress )
    {
        this.toAddress = toAddress;
    }
    
}
