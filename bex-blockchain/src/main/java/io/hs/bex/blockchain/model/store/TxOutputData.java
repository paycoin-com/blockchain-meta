package io.hs.bex.blockchain.model.store;

public class TxOutputData
{
    private String hash;
    private String address;
    private double value = 0;
    private long index = 0;
     
    public String getHash()
    {
        return hash;
    }
    public void setHash( String hash )
    {
        this.hash = hash;
    }
    public String getAddress()
    {
        return address;
    }
    public void setAddress( String address )
    {
        this.address = address;
    }
    public double getValue()
    {
        return value;
    }
    public void setValue( double value )
    {
        this.value = value;
    }
    public long getIndex()
    {
        return index;
    }
    public void setIndex( long index )
    {
        this.index = index;
    }
   
}
