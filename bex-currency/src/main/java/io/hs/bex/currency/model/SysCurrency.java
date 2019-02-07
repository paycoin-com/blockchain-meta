package io.hs.bex.currency.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.hs.bex.common.utils.StringUtils;


@JsonFormat( shape = JsonFormat.Shape.OBJECT )
@JsonIgnoreProperties( ignoreUnknown = true )
public enum SysCurrency
{
    // ---------------------------------------------------

    OTHER( 0, "Other", "", CurrencyType.OTHER ),

    BTC( 1, "Bitcoin", "U+20BF", CurrencyType.DIGITAL ),
    ETH( 2, "Ethereum", "U+039E", CurrencyType.DIGITAL ),
    BCH( 3, "Bitcoin-Cash", "U+20BF", CurrencyType.DIGITAL ),

    BAT( 4, "Basic-Attention-Token", "U+039E", CurrencyType.DIGITAL ),
    BNB( 5, "Binance Coin", "U+039E", CurrencyType.DIGITAL ),
    BNT( 6, "Bancor", "U+039E",CurrencyType.DIGITAL ),
    DAI( 7, "Dai", "U+039E",CurrencyType.DIGITAL ),
    DGD( 8, "DigixDAO", "U+039E",CurrencyType.DIGITAL ),
    DGX( 9, "Digix-Gold", "U+039E",CurrencyType.DIGITAL ),
    EURS(10, "STASIS-EURS", "U+039E",CurrencyType.DIGITAL ),
    GNT( 11, "Golem", "U+039E",CurrencyType.DIGITAL ),
    GUSD( 12, "Gemini-Dollar", "U+039E",CurrencyType.DIGITAL ),
    HT(  13, "Huobi-Token", "U+039E",CurrencyType.DIGITAL ),
    KCS( 14, "KuCoin-Shares", "U+039E",CurrencyType.DIGITAL ),
    KNC( 15, "Kyber-Network", "U+039E",CurrencyType.DIGITAL ),
    LINK( 16, "ChainLink", "U+039E",CurrencyType.DIGITAL ),
    LOOM( 17, "Loom", "U+039E",CurrencyType.DIGITAL ),
    MANA( 18, "Decentraland", "U+039E",CurrencyType.DIGITAL ),
    MCO( 19, "Crypto.com", "U+039E",CurrencyType.DIGITAL ),
    MITH( 20, "Mithril", "U+039E",CurrencyType.DIGITAL ),
    MKR( 21, "Maker", "U+039E",CurrencyType.DIGITAL ),
    NEXO( 22, "Nexo", "U+039E",CurrencyType.DIGITAL ),
    NPXS( 23, "Pundi X", "U+039E",CurrencyType.DIGITAL ),
    OMG( 24, "OmiseGO", "U+039E",CurrencyType.DIGITAL ),
    PAX( 25, "Paxos Standard", "U+039E",CurrencyType.DIGITAL ),
    POLY( 26, "Polymath", "U+039E",CurrencyType.DIGITAL ),
    PPT( 27, "Populous", "U+039E",CurrencyType.DIGITAL ),
    R( 28, "Revain", "U+039E",CurrencyType.DIGITAL ),
    REP( 29, "Reputation", "U+039E",CurrencyType.DIGITAL ),
    SNT( 30, "StatusNetwork", "U+039E",CurrencyType.DIGITAL ),
    TUSD( 31, "TrueUSD", "U+039E",CurrencyType.DIGITAL ),
    USDC( 32, "USD-Coin", "U+039E",CurrencyType.DIGITAL ),
    WTC( 33, "Walton", "U+039E",CurrencyType.DIGITAL ),
    ZIL( 34, "Zilliqa", "U+039E",CurrencyType.DIGITAL ),
    ZRX( 35, "Ox", "U+039E",CurrencyType.DIGITAL ),
  
    USD( "U+0024" ),
    EUR( "U+20AC" ),
    GBP( "U+00A3" ),
    JPY( "U+00A5" ),
    CAD( "U+0024" ),
    AUD( "U+20B3" ),
    CNY( "U+00A5" ),
    CHF( "U+20A3" ),
    RUB( "U+20BD" ),
    KRW( "U+20A9" ),
    TRY( "U+20BA" );

    // ---------------------------------------------------

    private int codeNumeric = 0;
    private String displayName;
    private String symbolUnicode;
    private CurrencyType type;
    private boolean supported = false;
    
    private String details;
    

    private java.util.Currency internalIns = null;

    SysCurrency( String symbolUnicode )
    {
        internalIns = java.util.Currency.getInstance( this.name() );
        this.type = CurrencyType.FIAT;
        this.symbolUnicode = symbolUnicode;
    }

    SysCurrency( int codeNumeric, String displayName, String symbolUnicode, CurrencyType type )
    {
        this.codeNumeric = codeNumeric;
        this.displayName = displayName;
        this.type = type;
        this.symbolUnicode = symbolUnicode;
    }

    
    @JsonProperty( "code_numeric" )
    public int getCodeNumeric()
    {
        if( internalIns != null )
            return internalIns.getNumericCode();

        return codeNumeric;
    }

    public CurrencyType getType()
    {
        return type;
    }

    @JsonProperty( "display_name" )
    public String getDisplayName()
    {
        if( internalIns != null )
            return internalIns.getDisplayName();

        return displayName;
    }

    @JsonProperty( "code" )
    public String getCode()
    {
        return name().toUpperCase();
    }
    
    @JsonIgnore
    public String getUid()
    {
        return this.name().toLowerCase() + "-" + this.displayName.toLowerCase();
    }

    @JsonProperty( "symbol_ucode" )
    public String getSymbolUnicode()
    {
        return symbolUnicode;
    }
    
    public void setSymbolUnicode( String symbolUnicode )
    {
        this.symbolUnicode = symbolUnicode;
    }

    @JsonIgnore
    public String getSymbol()
    {
        return StringUtils.toUnicodeValue( symbolUnicode );
    }
    
    @JsonIgnore
    public boolean isSupported()
    {
        return supported;
    }

    public void setSupported( boolean supported )
    {
        this.supported = supported;
    }

    public static SysCurrency find( String code )
    {
        for( SysCurrency currency: SysCurrency.values() )
        {
            if( currency.name().toLowerCase().equals( code.toLowerCase() ) )
            {
                return currency;
            }
        }

        return null;
    }
    
    public String getDetails()
    {
        return details;
    }

    public void setDetails( String details )
    {
        this.details = details;
    }

    @JsonCreator
    public static SysCurrency fromCode( @JsonProperty( "code" ) String code, 
                                        @JsonProperty( "symbol_ucode" ) String symbolUnicode )
    {
        SysCurrency currency =  java.util.Arrays.stream(SysCurrency.values())
                .filter( v -> v.name().equals( code )).findFirst().orElse( SysCurrency.OTHER );
        
        currency.setSymbolUnicode( symbolUnicode );
        currency.setSupported( true );
        
        return currency;
    }

}
