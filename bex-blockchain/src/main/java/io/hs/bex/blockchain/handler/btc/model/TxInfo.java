package io.hs.bex.blockchain.handler.btc.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties( ignoreUnknown = true )
public class TxInfo implements Comparable<TxInfo>
{
    @JsonProperty( "rate" )
    private long feeRate = 0;
    
    public long getFeeRate()
    {
        return feeRate;
    }

    public void setFeeRate( long feeRate )
    {
        this.feeRate = feeRate;
    }

    @Override
    public int compareTo( TxInfo tx )
    {
        if( tx == null)
            return 0;
        
        return Long.compare( feeRate, tx.getFeeRate() );
    }

    @Override
    public String toString()
    {
        return "TxInfo [fee=" + feeRate + "]";
    }
    
}
