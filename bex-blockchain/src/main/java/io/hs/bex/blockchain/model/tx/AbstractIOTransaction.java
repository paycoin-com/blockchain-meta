package io.hs.bex.blockchain.model.tx;

import io.hs.bex.blockchain.model.AbstractMessage;
import io.hs.bex.common.model.DigitalCurrency;
import io.hs.bex.common.model.DigitalCurrencyType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractIOTransaction extends AbstractMessage
{
    private int index = 0;
    
    private TransactionIOType type;

    private boolean cached = false;

    @JsonIgnore
    private GenericTransaction parent;
    
    @JsonProperty("amount")
    private DigitalCurrency amount;
    
    public AbstractIOTransaction( GenericTransaction transaction, TransactionIOType type )
    {
        this.parent = transaction;
        this.amount = new DigitalCurrency( 0, getCurrencyType() );
        this.type = type;
    }

    public DigitalCurrency getAmount()
    {
        return amount;
    }

    public void setAmount( DigitalCurrency amount )
    {
        this.amount = amount;
    }

    @JsonProperty("currency_type")
    public DigitalCurrencyType getCurrencyType()
    {
        if(parent != null)
            return parent.getCurrencyType();
        else
            return DigitalCurrencyType.UNDEFINED;
    }

    public GenericTransaction getParent()
    {
        return parent;
    }

    public void setParent( GenericTransaction parent )
    {
        this.parent = parent;
    }

    public boolean isCached()
    {
        return cached;
    }

    public void setCached( boolean cached )
    {
        this.cached = cached;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public TransactionIOType getType()
    {
        return type;
    }

    public void setType( TransactionIOType type )
    {
        this.type = type;
    }

   
}
