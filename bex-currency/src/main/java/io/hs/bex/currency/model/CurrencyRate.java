package io.hs.bex.currency.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.hs.bex.common.utils.StringUtils;

@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( Include.NON_NULL )
public class CurrencyRate
{
    @JsonIgnore
    private Instant date =  Instant.now();
    
    @JsonIgnore
    private SysCurrency currency;
    
    private float rate = 0;
    
    @JsonIgnore
    private SysCurrency targetCurrency;
    
    public CurrencyRate(){}
    
    public CurrencyRate( SysCurrency currency )
    {
        this.currency = currency;
    }
    
    public CurrencyRate( SysCurrency currency, SysCurrency targetCurrency, float rate )
    {
        this.currency = currency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
    
    public CurrencyRate( Instant date, SysCurrency currency, SysCurrency targetCurrency, float rate )
    {
        this.currency = currency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.date = date;
    }

    public SysCurrency getCurrency()
    {
        return currency;
    }

    public void setCurrency( SysCurrency currency )
    {
        this.currency = currency;
    }

    public float getRate()
    {
        return rate;
    }

    public void setRate( float rate )
    {
        this.rate = rate;
    }

    public SysCurrency getTargetCurrency()
    {
        return targetCurrency;
    }

    public void setTargetCurrency( SysCurrency targetCurrency )
    {
        this.targetCurrency = targetCurrency;
    }

    @JsonIgnore
    public Instant getDate()
    {
        return date;
    }
    
    @JsonIgnore
    public LocalDateTime getLocalDateTime()
    {
        return LocalDateTime.ofInstant( date, ZoneId.systemDefault() );
    }
    
    @JsonProperty("date_str")
    public String getDateStr()
    {
        return  StringUtils.instantToString( date );
    }
    
    @JsonProperty("rate_str")
    public String getRateStr()
    {
        return StringUtils.floatToString( rate );
    }

    @JsonProperty("date")
    public long getDateEpoch()
    {
        return date.toEpochMilli();
    }

    public void setDate( Instant date )
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return "CurrencyRate [currency=" + currency + ", rate=" + rate + ", targetCurrency=" + targetCurrency 
                + ", date:" + StringUtils.instantToString( date ) + "]";
    }
   
}
