package io.hs.bex.blockchain.model;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.hs.bex.blockchain.model.tx.GenericTransaction;
import io.hs.bex.common.model.DigitalCurrency;
import io.hs.bex.common.model.DigitalCurrencyType;
import io.hs.bex.common.utils.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class GenericBlock extends AbstractMessage
{
    @JsonProperty("prev_block_hash")
    private String prevBlockHash;
    
    @JsonProperty("merkle_root")
    private String merkleRoot;
    
    private Instant time;
    
    @JsonProperty("difficulty_target")
    private long difficultyTarget = 0;
    
    private long nonce = 0;
    
    private long height = 0;
    
    @JsonProperty("currency_type")
    private DigitalCurrencyType currencyType;
    
    private List<GenericTransaction> transactions;
    
    public GenericBlock( DigitalCurrencyType digitalCurrencyType )
    {
        this.transactions = new LinkedList<GenericTransaction>();
        this.currencyType = digitalCurrencyType;
    }   
    
    public String getPrevBlockHash()
    {
        return prevBlockHash;
    }

    public void setPrevBlockHash( String prevBlockHash )
    {
        this.prevBlockHash = prevBlockHash;
    }

    public String getMerkleRoot()
    {
        return merkleRoot;
    }

    public void setMerkleRoot( String merkleRoot )
    {
        this.merkleRoot = merkleRoot;
    }

    public Instant getTime()
    {
        return time;
    }
    
    @JsonProperty("time_string")
    public String getTimeAsString()
    {
        return StringUtils.instantToString( time );
    }

    public void setTime( Instant time )
    {
        this.time = time;
    }
    
    public void setTime( Date time )
    {
        this.time = time.toInstant();
    }

    public long getDifficultyTarget()
    {
        return difficultyTarget;
    }

    public void setDifficultyTarget( long difficultyTarget )
    {
        this.difficultyTarget = difficultyTarget;
    }

    public long getNonce()
    {
        return nonce;
    }

    public void setNonce( long nonce )
    {
        this.nonce = nonce;
    }

    public List<GenericTransaction> getTransactions()
    {
        return transactions;
    }

    public void setTransactions( List<GenericTransaction> transactions )
    {
        this.transactions = transactions;
    }

    public long getHeight()
    {
        return height;
    }

    public void setHeight( long height )
    {
        this.height = height;
    }

    public DigitalCurrencyType getCurrencyType()
    {
        return currencyType;
    }
    
    @JsonProperty("input_sum")
    public DigitalCurrency getInputSum() 
    {        
        DigitalCurrency inputTotal = new DigitalCurrency( 0, getCurrencyType());
        
        for (GenericTransaction tx: transactions) 
        {
            DigitalCurrency inputValue = tx.getInputSum();
            
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
        
        for (GenericTransaction tx: transactions) 
        {
            DigitalCurrency outputValue = tx.getOutputSum();
            
            if (outputValue != null) 
            {
                outputTotal.add( outputValue );
            }
        }

        return outputTotal;
    }

            
}
