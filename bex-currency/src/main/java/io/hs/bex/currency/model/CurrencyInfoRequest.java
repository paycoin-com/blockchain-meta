package io.hs.bex.currency.model;

import java.time.Instant;
import java.time.ZoneId;

import io.hs.bex.common.utils.StringUtils;

public class CurrencyInfoRequest
{
    private SysCurrency sourceCurrency;
    private SysCurrency targetCurrency;
    
    private TimePeriod period = TimePeriod.DAY;
    
    private Instant dateTo =  Instant.ofEpochMilli( System.currentTimeMillis() );
    private int limit = 30;
    
    public CurrencyInfoRequest() 
    {
        
    }
    
    public CurrencyInfoRequest( SysCurrency sourceCurrency, SysCurrency targetCurrency, TimePeriod period, Instant dateTo,
            int limit )
    {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.period = period;
        this.dateTo = dateTo;
        this.limit = limit;
    }
    
    public CurrencyInfoRequest( String sourceCurrency, String targetCurrency, String periodStr, String toDateStr,
            int limit )
    {
        this.sourceCurrency = SysCurrency.find( sourceCurrency );
        this.targetCurrency = SysCurrency.find( targetCurrency );
        
        this.period = TimePeriod.find( periodStr );
        this.dateTo = StringUtils.stringToDate( toDateStr ).atZone( ZoneId.systemDefault() ).toInstant();
        this.limit = limit;
    }

    
    public CurrencyInfoRequest( SysCurrency sourceCurrency, SysCurrency targetCurrency )
    {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
    }

    public SysCurrency getSourceCurrency()
    {
        return sourceCurrency;
    }

    public void setSourceCurrency( SysCurrency sourceCurrency )
    {
        this.sourceCurrency = sourceCurrency;
    }

    public SysCurrency getTargetCurrency()
    {
        return targetCurrency;
    }

    public void setTargetCurrency( SysCurrency targetCurrency )
    {
        this.targetCurrency = targetCurrency;
    }

    public Instant getDateTo()
    {
        return dateTo;
    }

    public void setDateTo( Instant dateTo )
    {
        this.dateTo = dateTo;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit( int limit )
    {
        this.limit = limit;
    }

    public TimePeriod getPeriod()
    {
        return period;
    }

    public void setPeriod( TimePeriod period )
    {
        this.period = period;
    }

    @Override
    public String toString()
    {
        return "CurrencyInfoRequest [sourceCurrency=" + sourceCurrency + ", targetCurrency=" + targetCurrency
                + ", period=" + period + ", dateTo=" + dateTo + ", limit=" + limit + "]";
    }
    
        
}
