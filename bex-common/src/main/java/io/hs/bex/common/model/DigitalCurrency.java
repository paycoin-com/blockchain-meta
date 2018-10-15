package io.hs.bex.common.model;

import io.hs.bex.common.utils.StringUtils;

public class DigitalCurrency
{
    private DigitalCurrencyType type = DigitalCurrencyType.UNDEFINED;
    
    private double value = 0;
    
    
    public DigitalCurrency( double value, DigitalCurrencyType type )
    {
        this.value = value;
        this.type = type;
    }
    
    public DigitalCurrencyType getType()
    {
        return type;
    }

    public void setType( DigitalCurrencyType type )
    {
        this.type = type;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue( double value )
    {
        this.value = value;
    }
    
    public void add( final DigitalCurrency dc ) 
    {
        this.setValue( this.getValue() + dc.getValue() );
    }
    
    public DigitalCurrency subtract( final DigitalCurrency dc ) 
    {
        this.setValue( this.getValue() - dc.getValue() );
        
        return this;
    }
    
    public String valueAsString() 
    {
        return StringUtils.doubleToString( value );
    }
}
