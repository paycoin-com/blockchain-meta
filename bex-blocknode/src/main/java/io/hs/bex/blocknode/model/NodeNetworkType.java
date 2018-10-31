package io.hs.bex.blocknode.model;


public enum NodeNetworkType
{
    MAINNET( 1 ),
    TESTNET( 2 ),
    REGTEST( 3 );

    private short id = 0;

    NodeNetworkType( int id )
    {
        this.id = (short) id;
    }

    public short getId()
    {
        return id;
    }

}
