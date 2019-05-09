package io.hs.bex.blockchain.model.store;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table( name = "tb_estimaterate" )
@JsonIgnoreProperties( ignoreUnknown = true )
public class EstimateFeeRate
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;
    
    @Column( name = "coind_id" )
    public short coinId;
    
    @Column( name = "timestamp" )
    public Instant timestamp;
    
    @Column( name = "low" )
    public long lowPriority;
    
    @Column( name = "medium" )
    private long meidumPriority;
    
    @Column( name = "high" )
    private long highPriority;
    
    // *********************************************
    @JsonIgnore
    @OneToMany( mappedBy="estimateFeeRate", fetch = FetchType.LAZY , cascade = CascadeType.ALL )
    private List<EstimateFeeRateDetails> details;
    // *********************************************

    // *********************************************
    @JsonIgnore
    @OneToMany( mappedBy="estimateFeeRate", fetch = FetchType.LAZY , cascade = CascadeType.ALL )
    private List<PredictedFeeValues> predictedValues;
    // *********************************************

    // *********************************************
    @JsonIgnore
    @OneToMany( mappedBy="estimateFeeRate", fetch = FetchType.LAZY , cascade = CascadeType.ALL )
    private List<FeeEstimationData> feeEstimationDatas;
    // *********************************************
    
    public EstimateFeeRate() {}
    
    public EstimateFeeRate( long lowPriority, long meidumPriority, long highPriority )
    {
        this.lowPriority = lowPriority;
        this.meidumPriority = meidumPriority;
        this.highPriority = highPriority;
    }
    
    public EstimateFeeRate( EstimateFeeRate eFeeRate )
    {
        this.lowPriority = eFeeRate.getLowPriority();
        this.meidumPriority = eFeeRate.getMeidumPriority();
        this.highPriority = eFeeRate.getHighPriority();
    }
    
    
    @JsonIgnore
    public void setValues( EstimateFeeRate eFeeRate )
    {
        this.lowPriority = eFeeRate.getLowPriority();
        this.meidumPriority = eFeeRate.getMeidumPriority();
        this.highPriority = eFeeRate.getHighPriority();
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Instant timestamp )
    {
        this.timestamp = timestamp;
    }

    public long getLowPriority()
    {
        return lowPriority;
    }

    public void setLowPriority( long lowPriority )
    {
        this.lowPriority = lowPriority;
    }

    public long getMeidumPriority()
    {
        return meidumPriority;
    }

    public void setMeidumPriority( long meidumPriority )
    {
        this.meidumPriority = meidumPriority;
    }

    public long getHighPriority()
    {
        return highPriority;
    }

    public void setHighPriority( long highPriority )
    {
        this.highPriority = highPriority;
    }

    public short getCoinId()
    {
        return coinId;
    }

    public void setCoinId( short coinId )
    {
        this.coinId = coinId;
    }

    public List<EstimateFeeRateDetails> getDetails()
    {
        return details;
    }

    public void setDetails( List<EstimateFeeRateDetails> details )
    {
        this.details = details;
    }
    
    
    public List<PredictedFeeValues> getPredictedValues()
    {
        return predictedValues;
    }

    public void setPredictedValues( List<PredictedFeeValues> predictedValues )
    {
        this.predictedValues = predictedValues;
    }

    public void addDetails( EstimateFeeRateDetails detailsData )
    {
        if(details == null)
            details = new ArrayList<EstimateFeeRateDetails>();
        
        details.add( detailsData );
        detailsData.setEstimateFeeRate( this );
    }
    
    public void addEstimationData( FeeEstimationData feeEstimationData )
    {
        if(feeEstimationDatas == null)
            feeEstimationDatas = new ArrayList<FeeEstimationData>();
        
        feeEstimationDatas.add( feeEstimationData );
        feeEstimationData.setEstimateFeeRate( this );
    }
    
    public void addPredcitedValues( PredictedFeeValues predictedValueData )
    {
        if(predictedValues == null)
            predictedValues = new ArrayList<PredictedFeeValues>();
        
        predictedValues.add( predictedValueData );
        predictedValueData.setEstimateFeeRate( this );
    }
    
}
