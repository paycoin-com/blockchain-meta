package io.hs.bex.blockchain.model.store;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table( name = "tb_estimateratedetails" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class EstimateFeeRateDetails
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;

    @Column( name = "range" )
    private int range;
    
    @Column( name = "sum" )
    private int sum;
    
    @Column( name = "timestamp" )
    private Instant timestamp;
    
    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "estimaterate_id")
    private EstimateFeeRate estimateFeeRate;
    
    public EstimateFeeRateDetails() {}
    
    public EstimateFeeRateDetails( int range, long time, int sum )
    {
        this.range = range;
        this.sum = sum;
        this.timestamp = Instant.ofEpochMilli( time );
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public int getRange()
    {
        return range;
    }

    public void setRange( int range )
    {
        this.range = range;
    }

    public EstimateFeeRate getEstimateFeeRate()
    {
        return estimateFeeRate;
    }

    public void setEstimateFeeRate( EstimateFeeRate estimateFeeRate )
    {
        this.estimateFeeRate = estimateFeeRate;
    }

    public int getSum()
    {
        return sum;
    }

    public void setSum( int sum )
    {
        this.sum = sum;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Instant timestamp )
    {
        this.timestamp = timestamp;
    }

}
