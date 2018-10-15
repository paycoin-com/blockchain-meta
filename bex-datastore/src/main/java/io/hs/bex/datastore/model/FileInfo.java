package io.hs.bex.datastore.model;


public class FileInfo
{
    private String hash;
    private String name;
    boolean directory = false;
    
    private long size = 0;

    
    public FileInfo( String hash, long size, boolean directory )
    {
        this.hash = hash;
        this.directory = directory;
        this.size = size;
    }

    public String getHash()
    {
        return hash;
    }

    public void setHash( String hash )
    {
        this.hash = hash;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean isDirectory()
    {
        return directory;
    }

    public void setDirectory( boolean directory )
    {
        this.directory = directory;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize( long size )
    {
        this.size = size;
    }
    
    
}
