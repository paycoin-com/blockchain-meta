package io.hs.bex.blockchain.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table( 
        name = "tb_blockchain" ,
        uniqueConstraints={ @UniqueConstraint(columnNames={"block_hash", "node_id"})} 
      ) 
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockChainData
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;

    @Column( name = "block_hash" )
    private String blockHash;
    
    @Column( name = "node_id" )
    private short nodeId;
    
    @Column( name = "status" )
    private short status = 0 ;
    
    @Column( name = "height" )
    private long height;
    
    @Column( name = "data" )
    private byte[] data;
    
    
    public BlockChainData()
    {
    }
    
    public BlockChainData( short nodeId, String blockHash, long height , byte[] data )
    {
        this.height = height;
        this.blockHash = blockHash;
        this.data = data;
        this.nodeId = nodeId;
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getBlockHash()
    {
        return blockHash;
    }

    public void setBlockHash( String blockHash )
    {
        this.blockHash = blockHash;
    }

    public long getHeight()
    {
        return height;
    }

    public void setHeight( long height )
    {
        this.height = height;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData( byte[] data )
    {
        this.data = data;
    }

    public short getNodeId()
    {
        return nodeId;
    }

    public void setNodeId( short nodeId )
    {
        this.nodeId = nodeId;
    }

    public short getStatus()
    {
        return status;
    }

    public void setStatus( short status )
    {
        this.status = status;
    }
    
}
