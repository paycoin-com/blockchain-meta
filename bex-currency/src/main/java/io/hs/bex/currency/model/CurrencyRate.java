package io.hs.bex.currency.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class CurrencyRate
{
    private LocalDateTime date = LocalDateTime.now(); 
    
    private SysCurrency currency;
    
    private float rate = 0;
    
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
    
    public CurrencyRate( LocalDateTime date, SysCurrency currency, SysCurrency targetCurrency, float rate )
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

    public LocalDateTime getDate()
    {
        return date;
    }

    public void setDate( LocalDateTime date )
    {
        this.date = date;
    }
   
}
