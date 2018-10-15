package io.hs.bex.blocknode.model;

import java.util.UUID;
import io.hs.bex.common.model.DigitalCurrencyType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties( ignoreUnknown = true )
public class Node
{
    private int id = 0;
    
    private String uuid = UUID.randomUUID().toString();
    
    private String name;
    
    private boolean FullVerificationMode = false;
    
    private NodeProvider provider;
    
    private NodeNetwork network;
    
    private NodeStatus status = new NodeStatus();
    
    private Object blockChain;
    
    private Object peerGroup; 
    
    public Node()
    {
        provider = new NodeProvider( DigitalCurrencyType.BTC, NodeNetworkType.TESTNET );
        network = new NodeNetwork( NodeNetworkType.TESTNET );
    }
    
    public Node( DigitalCurrencyType digitalCurrencyType, NodeNetworkType networkType )
    {
        provider = new NodeProvider( digitalCurrencyType, networkType );
        network = new NodeNetwork( networkType );
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public NodeProvider getProvider()
    {
        return provider;
    }

    public void setProvider( NodeProvider provider )
    {
        this.provider = provider;
    }

    public NodeNetwork getNetwork()
    {
        return network;
    }

    public void setNetwork( NodeNetwork network )
    {
        this.network = network;
    }

    public NodeStatus getStatus()
    {
        return status;
    }

    public void setStatus( NodeStatus status )
    {
        this.status = status;
    }

    public NodeState getState()
    {
        return status.getOverallState();
    }

    public void setState( NodeState state )
    {
        this.status.setOverallState( state );
    }

    public boolean isFullVerificationMode()
    {
        return FullVerificationMode;
    }

    public void setFullVerificationMode( boolean fullVerificationMode )
    {
        FullVerificationMode = fullVerificationMode;
    }

    public Object getBlockChain()
    {
        return blockChain;
    }

    public void setBlockChain( Object blockChain )
    {
        this.blockChain = blockChain;
    }

    public Object getPeerGroup()
    {
        return peerGroup;
    }

    public void setPeerGroup( Object peerGroup )
    {
        this.peerGroup = peerGroup;
    }
        
}
