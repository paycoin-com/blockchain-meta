package io.hs.bex.currency.model;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.hs.bex.common.utils.StringUtils;

public class CurrencyInfoRequest
{
    private List<SysCurrency> targetCurrencies = new ArrayList<SysCurrency>();
    private List<SysCurrency> sourceCurrencies = new ArrayList<SysCurrency>();
    
    private TimePeriod period = TimePeriod.DAY;
    
    private Instant dateTo =  Instant.ofEpochMilli( System.currentTimeMillis() );
    private int limit = 30;
    
    public CurrencyInfoRequest() 
    {
        
    }
    
    public CurrencyInfoRequest( SysCurrency sourceCurrency, SysCurrency targetCurrency, TimePeriod period, Instant dateTo,
            int limit )
    {
        this.sourceCurrencies.add( sourceCurrency );
        this.targetCurrencies.add( targetCurrency );
        this.period = period;
        this.dateTo = dateTo;
        this.limit = limit;
    }
    
    public CurrencyInfoRequest( String sourceCurrency, String targetCurrency, String periodStr, String toDateStr,
            int limit )
    {
        this.sourceCurrencies.add( SysCurrency.find( sourceCurrency ) );
        this.targetCurrencies.add( SysCurrency.find( targetCurrency ) );
        
        this.period = TimePeriod.find( periodStr );
        this.dateTo = StringUtils.stringToDate( toDateStr ).atZone( ZoneId.systemDefault() ).toInstant();
        this.limit = limit;
    }

    public CurrencyInfoRequest( SysCurrency sourceCurrency, SysCurrency targetCurrency )
    {
        this.sourceCurrencies.add( sourceCurrency );
        this.targetCurrencies.add( targetCurrency );
    }
    
    public void clearCurrencies() 
    {
        sourceCurrencies.clear();
        targetCurrencies.clear();
    }

    public List<SysCurrency> getSourceCurrencies()
    {
        return sourceCurrencies;
    }

    public void setSourceCurrencies( List<SysCurrency> sourceCurrencies )
    {
        this.sourceCurrencies = sourceCurrencies;
    }

    public List<SysCurrency> getTargetCurrencies()
    {
        return targetCurrencies;
    }
    

    public void setTargetCurrencies( List<SysCurrency> targetCurrencies )
    {
        this.targetCurrencies = targetCurrencies;
    }
    
    public String getSourceCcyCode()
    {
        if( sourceCurrencies.get(0) != null) 
        {
            return sourceCurrencies.get(0).getCode();
        }
        else
            return "";
    }
    
    public String getTargetCcyCode()
    {
        if( targetCurrencies.get(0) != null) 
        {
            return targetCurrencies.get(0).getCode();
        }
        else
            return "";
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
    
    public String joinTargetCurrencies( String separator ) 
    {
        return targetCurrencies.stream().map(currency -> currency.getCode())
                .collect(Collectors.joining( separator ));
    }
    
    public String joinSourceCurrencies( String separator ) 
    {
        return sourceCurrencies.stream().map(currency -> currency.getCode())
                .collect(Collectors.joining( separator ));
    }


    @Override
    public String toString()
    {
        return "CurrencyInfoRequest [sourceCurrency=" + sourceCurrencies.toString() 
                + ", targetCurrency=" + targetCurrencies.toString()
                + ", period=" + period + ", dateTo=" + dateTo + ", limit=" + limit + "]";
    }
        
}
