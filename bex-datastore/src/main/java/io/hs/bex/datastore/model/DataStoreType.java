package io.hs.bex.datastore.model;


public enum DataStoreType
{
    FILESYSTEM( 0, "FileSystemHandler" ), 
    DATABASE( 1, "DatabaseHandler" ), 
    IPFS( 2 , "IpfsHandler");

    private short id;
    private String handlerName;

    private DataStoreType( int id, String handlerName )
    {
        this.id = (short)id;
        this.handlerName = handlerName;
    }

    public short getId()
    {
        return id;
    }

    public String getHandlerName()
    {
        return handlerName;
    }

}
