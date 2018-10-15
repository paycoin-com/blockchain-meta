package io.hs.bex.currency.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public enum CurrencyType
{
    OTHER( 0 ), 
    DIGITAL( 1 ), 
    FIAT( 2 );

    int id;

    CurrencyType( int id )
    {
        this.id = id;
    }

    @JsonProperty( "id" )
    public int getId()
    {
        return id;
    }

    @JsonCreator
    static CurrencyType fromId( int value )
    {
        return java.util.Arrays.stream( CurrencyType.values() ).filter( e -> e.id == value ).findFirst().get();
    }

}
