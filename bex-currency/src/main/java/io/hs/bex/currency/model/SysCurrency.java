package io.hs.bex.currency.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

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
    DASH( 4, "Dash", "U+20BF", CurrencyType.DIGITAL ),

    //AURA( 5, "aurora-dao", "U+039E", CurrencyType.DIGITAL ),
    ANKR( 5, "ankr-network", "U+039E", CurrencyType.DIGITAL ),
    BAT( 6, "Basic-Attention-Token", "U+039E", CurrencyType.DIGITAL ),
    BNB( 7, "Binance-coin", "U+039E", CurrencyType.DIGITAL ),
    BNT( 8, "Bancor", "U+039E",CurrencyType.DIGITAL ),
    BTCB( 8, "Bitcoin-bep2", "U+039E",CurrencyType.DIGITAL ),
    CAS( 9, "Cashaa", "U+039E",CurrencyType.DIGITAL ),
    CRO( 9, "Cryptocom-chain", "U+039E",CurrencyType.DIGITAL ),
    CRPT( 9, "Crypterium", "U+039E",CurrencyType.DIGITAL ),
    DAI( 10, "Dai", "U+039E",CurrencyType.DIGITAL ),
    DGD( 11, "Digixdao", "U+039E",CurrencyType.DIGITAL ),
    DGX( 12, "Digix-gold-token", "U+039E",CurrencyType.DIGITAL ),
    ELF( 13, "Aelf", "U+039E", CurrencyType.DIGITAL ),
    ENJ( 14, "Enjin-coin", "U+039E",CurrencyType.DIGITAL ),
    EOS( 14, "Eos", "U+039E",CurrencyType.DIGITAL ),
    EOSDT( 14, "Eosdt", "U+039E",CurrencyType.DIGITAL ),
    EURS(15, "Stasis-eurs", "U+039E",CurrencyType.DIGITAL ),
    GNT( 16, "Golem", "U+039E",CurrencyType.DIGITAL ),
    GUSD( 17, "Gemini-dollar", "U+039E",CurrencyType.DIGITAL ),
    GTO( 17, "Gifto", "U+039E",CurrencyType.DIGITAL ),
    HOT( 18, "Holo" , "Holo", "U+039E",CurrencyType.DIGITAL ),
    HT(  19, "Huobi-Token", "U+039E",CurrencyType.DIGITAL ),
    IDEX( 20, "IDEX", "U+039E",CurrencyType.DIGITAL ),
    IDXM( 21, "Idex-membership", "U+039E",CurrencyType.DIGITAL ),
    IQ( 21, "Everipedia", "U+039E",CurrencyType.DIGITAL ),
    KCS( 22, "Kucoin-shares", "U+039E",CurrencyType.DIGITAL ),
    KNC( 23, "Kyber-Network", "U+039E",CurrencyType.DIGITAL ),
    LINK( 24, "ChainLink", "U+039E",CurrencyType.DIGITAL ),
    LOOM( 25, "Loom-network", "U+039E",CurrencyType.DIGITAL ),
    LRC( 26, "Loopring", "U+039E",CurrencyType.DIGITAL ),
    MANA( 27, "Decentraland", "U+039E",CurrencyType.DIGITAL ),
    MCO( 28, "mco", "U+039E",CurrencyType.DIGITAL ),
    MEETONE( 28, "Meetone", "U+039E",CurrencyType.DIGITAL ),
    MITH( 29, "Mithril", "U+039E",CurrencyType.DIGITAL ),
    MKR( 30, "Maker", "U+039E",CurrencyType.DIGITAL ),
    NEXO( 31, "Nexo", "U+039E",CurrencyType.DIGITAL ),
    NPXS( 32, "Pundi-x", "U+039E",CurrencyType.DIGITAL ),
    NUT( 32, "Nut", "U+039E",CurrencyType.DIGITAL ),
    OMG( 33, "OmiseGO", "U+039E",CurrencyType.DIGITAL ),
    ORBS( 34, "Orbs", "U+039E",CurrencyType.DIGITAL ),
    PAX( 35, "Paxos-standard-token", "U+039E",CurrencyType.DIGITAL ),
    PGL( 35, "Prospectors-Gold", "U+039E",CurrencyType.DIGITAL ),
    POLY( 36, "Polymath", "U+039E",CurrencyType.DIGITAL ),
    PPT( 37, "Populous", "U+039E",CurrencyType.DIGITAL ),
    PTI( 37, "Paytomat", "U+039E",CurrencyType.DIGITAL ),
    R( 38, "Revain", "U+039E",CurrencyType.DIGITAL ),
    REP( 39, "Augur", "U+039E",CurrencyType.DIGITAL ),
    SNT( 40, "Status", "U+039E",CurrencyType.DIGITAL ),
    TUSD( 41, "TrueUSD", "U+039E",CurrencyType.DIGITAL ),
    USDC( 42, "USD-Coin", "U+039E",CurrencyType.DIGITAL ),
    USDT( 43, "Tether", "U+039E",CurrencyType.DIGITAL ),
    WAX( 44, "WAX", "U+039E",CurrencyType.DIGITAL ),
    WTC( 45, "Waltonchain", "U+039E",CurrencyType.DIGITAL ),
    //XBASE( 47, "eterbase", "U+039E",CurrencyType.DIGITAL ),
    ZIL( 46, "Zilliqa", "U+039E",CurrencyType.DIGITAL ),
    ZRX( 47, "0x", "U+039E",CurrencyType.DIGITAL ),

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
    private String secondaryCode;
    

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
    
    SysCurrency( int codeNumeric, String secondaryCode, String displayName, String symbolUnicode, CurrencyType type )
    {
        this.secondaryCode = secondaryCode;
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
    

    public String getSecondaryCode()
    {
        if(!Strings.isNullOrEmpty( secondaryCode ))
            return secondaryCode.toUpperCase();
        
        return name().toUpperCase();
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
