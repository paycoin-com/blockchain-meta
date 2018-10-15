package io.hs.bex.blockchain.model.tx;

import java.util.ArrayList;
import java.util.List;

import io.hs.bex.blockchain.model.AbstractMessage;
import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.common.model.DigitalCurrency;
import io.hs.bex.common.model.DigitalCurrencyType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class GenericTransaction extends AbstractMessage
{
    private String memo;
    
    private TransactionStatus status = TransactionStatus.UNDEFINED; 
    
    @JsonProperty("tx_inputs")
    private List<GenericTxInput> txInputs;
    
    @JsonProperty("tx_outputs")
    private List<GenericTxOutput> txOutputs;
    
    @JsonIgnore
    private GenericBlock parentBlock;
    
    public GenericTransaction( GenericBlock block ) 
    {
        this.parentBlock = block;
        this.parentBlock.getTransactions().add( this );
        
        txInputs = new ArrayList<GenericTxInput>();
        txOutputs = new ArrayList<GenericTxOutput>();
    }
   
    public List<GenericTxInput> getTxInputs()
    {
        return txInputs;
    }

    public void setTxInputs( List<GenericTxInput> txInputs )
    {
        this.txInputs = txInputs;
    }

    public List<GenericTxOutput> getTxOutputs()
    {
        return txOutputs;
    }

    public void setTxOutputs( List<GenericTxOutput> txOutputs )
    {
        this.txOutputs = txOutputs;
    }

    public TransactionStatus getStatus()
    {
        return status;
    }

    public void setStatus( TransactionStatus status )
    {
        this.status = status;
    }

    @JsonProperty("coinbase")
    public boolean isCoinBase() 
    {
        return txInputs.size() == 1 && txInputs.get(0).isCoinBase();
    }

    public String getMemo()
    {
        return memo;
    }

    public void setMemo( String memo )
    {
        this.memo = memo;
    }
    
    public GenericBlock getParentBlock()
    {
        return parentBlock;
    }

    public void setParentBlock( GenericBlock parentBlock )
    {
        this.parentBlock = parentBlock;
    }

    @JsonProperty("currency_type")
    public DigitalCurrencyType getCurrencyType()
    {
        if(parentBlock != null)
            return parentBlock.getCurrencyType();
        else
            return DigitalCurrencyType.UNDEFINED;
    }

    
    @JsonProperty("input_sum")
    public DigitalCurrency getInputSum() 
    {        
        DigitalCurrency inputTotal = new DigitalCurrency( 0, getCurrencyType());
        
        for (GenericTxInput input: txInputs) 
        {
            DigitalCurrency inputValue = input.getAmount();
            
            if (inputValue != null) 
            {
                inputTotal.add( inputValue );
            }
        }

        return inputTotal;
        
    }
    
    
    @JsonProperty("output_sum")
    public DigitalCurrency getOutputSum() 
    {
        DigitalCurrency outputTotal = new DigitalCurrency( 0, getCurrencyType());
        
        for ( GenericTxOutput output: txOutputs ) 
        {
            DigitalCurrency outputValue = output.getAmount();
            
            if (outputValue != null) 
            {
                outputTotal.add( outputValue );
            }
        }

        return outputTotal;
    }
    
    @JsonProperty("fee_sum")
    public DigitalCurrency getFeeSum() 
    {
        return getInputSum().subtract( getOutputSum() );
    }
    
}
