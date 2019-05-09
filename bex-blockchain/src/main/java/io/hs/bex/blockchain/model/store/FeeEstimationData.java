package io.hs.bex.blockchain.model.store;


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
@Table( name = "tb_feeestimationdata" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class FeeEstimationData
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;

    @Column( name = "range" )
    private int range;
    
    @Column( name = "fvalue" )
    private double value;
    
    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "estimaterate_id")
    private EstimateFeeRate estimateFeeRate;
    
    public FeeEstimationData() {}
    
    public FeeEstimationData( int range, double value )
    {
        super();
        this.range = range;
        this.value = value;
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

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }

    public EstimateFeeRate getEstimateFeeRate()
    {
        return estimateFeeRate;
    }

    public void setEstimateFeeRate( EstimateFeeRate estimateFeeRate )
    {
        this.estimateFeeRate = estimateFeeRate;
    }
    
    
}
