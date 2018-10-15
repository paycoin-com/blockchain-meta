package io.hs.bex.datastore.handler.ipfs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class FileInfoResponse
{
//    {
//        "Hash": "QmYNkCE8noWDtNpERf1jSeyGQRmzCC6qyYMu3PW4AwB1yT",
//        "Size": 0,
//        "CumulativeSize": 4426348,
//        "Blocks": 1,
//        "Type": "directory"
//    }
    
    @JsonProperty("Hash")
    private String hash;
    
    @JsonProperty("Size")
    private long size;
    
    @JsonProperty("CumulativeSize")
    private long cumulativeSize;
    
    @JsonProperty("Blocks")
    private int blocks;
    
    @JsonProperty("Type")
    private String type;

    public String getHash()
    {
        return hash;
    }

    public void setHash( String hash )
    {
        this.hash = hash;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize( long size )
    {
        this.size = size;
    }

    public long getCumulativeSize()
    {
        return cumulativeSize;
    }

    public void setCumulativeSize( long cumulativeSize )
    {
        this.cumulativeSize = cumulativeSize;
    }

    public int getBlocks()
    {
        return blocks;
    }

    public void setBlocks( int blocks )
    {
        this.blocks = blocks;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }
}
