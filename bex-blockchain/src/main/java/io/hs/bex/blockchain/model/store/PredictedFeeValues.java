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
@Table( name = "tb_predictedvalues" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class PredictedFeeValues
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;
    
    @Column( name = "range" )
    private int range;
    
    @Column( name = "predicted" )
    private int predicted;
    
    @Column( name = "prev_value" )
    private int prevValue;
    
    @Column( name = "predict_time" ) //mins
    private int predictTime;
    
    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn(name = "estimaterate_id")
    private EstimateFeeRate estimateFeeRate;
    
    public PredictedFeeValues() {}
    
    public PredictedFeeValues( int range, int predicted, int prevValue, long predictTime )
    {
        this.range = range;
        this.predicted = predicted;
        this.prevValue = prevValue;
        this.predictTime = (int) ( predictTime/1000/60);
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

    public int getPredicted()
    {
        return predicted;
    }

    public void setPredicted( int predicted )
    {
        this.predicted = predicted;
    }

    public int getPrevValue()
    {
        return prevValue;
    }

    public void setPrevValue( int prevValue )
    {
        this.prevValue = prevValue;
    }

    public int getPredictTime()
    {
        return predictTime;
    }

    public void setPredictTime( int predictTime )
    {
        this.predictTime = predictTime;
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
