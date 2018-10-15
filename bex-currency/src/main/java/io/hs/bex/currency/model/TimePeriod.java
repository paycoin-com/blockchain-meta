package io.hs.bex.currency.model;

public enum TimePeriod
{
    YEAR( 1 ),
    MONTH( 2 ),
    DAY( 3 ),
    HOUR( 4 ),
    MINUTE(5);

    private int id;

    TimePeriod( int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
    
    public static TimePeriod find( String searchName ) 
    {
        for( TimePeriod period:TimePeriod.values() ) 
        {
            if(period.name().toLowerCase().equals( searchName.toLowerCase() )) 
            {
                return period;
            }
        }
        
        return null;
    }

}
