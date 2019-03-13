package io.hs.bex.blockchain.handler.btc.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties( ignoreUnknown = true )
public class TxInfo implements Comparable<TxInfo>
{
    @JsonProperty( "fee" )
    private long fee = 0;

    public long getFee()
    {
        return fee;
    }

    public void setFee( long fee )
    {
        this.fee = fee;
    }

    @Override
    public int compareTo( TxInfo tx )
    {
        if( tx == null)
            return 0;
        
        return Long.compare( fee, tx.getFee() );
    }

    @Override
    public String toString()
    {
        return "TxInfo [fee=" + fee + "]";
    }
    
}
