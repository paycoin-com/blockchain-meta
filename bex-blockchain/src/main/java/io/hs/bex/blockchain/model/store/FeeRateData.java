package io.hs.bex.blockchain.model.store;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table( name = "tb_feeratedata" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeRateData
{
    @Id
    @Column( name = "height" )
    private long height;
    
    @Column( name = "date" )
    private Instant date;
    
    @Column( name = "value1" )
    private long value1;
    
    @Column( name = "value2" )
    private long value2;
    
    @Column( name = "value3" )
    private long value3;
    
    public FeeRateData() {}

    public long getHeight()
    {
        return height;
    }

    public void setHeight( long height )
    {
        this.height = height;
    }

    public Instant getDate()
    {
        return date;
    }

    public void setDate( Instant date )
    {
        this.date = date;
    }

    public long getValue1()
    {
        return value1;
    }

    public void setValue1( long value1 )
    {
        this.value1 = value1;
    }

    public long getValue2()
    {
        return value2;
    }

    public void setValue2( long value2 )
    {
        this.value2 = value2;
    }

    public long getValue3()
    {
        return value3;
    }

    public void setValue3( long value3 )
    {
        this.value3 = value3;
    }
    
}
