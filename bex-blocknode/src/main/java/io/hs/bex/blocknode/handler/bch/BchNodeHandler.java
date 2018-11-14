package io.hs.bex.blocknode.handler.bch;


import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;
import io.hs.bex.blocknode.model.NodeProvider;
import io.hs.bex.blocknode.model.NodeState;
import io.hs.bex.blocknode.model.NodeStatus;
import io.hs.bex.blocknode.model.OperationType;
import io.hs.bex.blocknode.service.api.GenericNodeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service( "BCH-NodeHandler" )
@Scope("prototype")
public class BchNodeHandler implements GenericNodeHandler
{

    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BchNodeHandler.class );
    // ---------------------------------
    
    private Node node = new Node();
    
    @Autowired
    private Environment env;
    
    private String host;
    
    @Override
    public Node init( NodeProvider nodeProvider )
    {
        String prefix = ""; 
        
        if( nodeProvider.getNetworkType() != NodeNetworkType.MAINNET)
            prefix = "-" + nodeProvider.getNetworkType().name().toLowerCase();
                
        host = env.getProperty( "node.bch" + prefix + ".host" );
        node.setProvider( nodeProvider );
        node.setId( nodeProvider.getId());
        node.setName( "BCH-" + nodeProvider.getNetworkType().name().toLowerCase() );
        node.getNetwork().setType( nodeProvider.getNetworkType() );
        node.getNetwork().setHost( host );
        
        start(); 
        
        logger.info( "Node {} started successfully. ", node.getName() );
     
        return node;
    }
    
    
    public Node start() 
    {
        setNodeStatus( node, NodeState.ACTIVE_NOTSYNC, OperationType.INIT, "Successfully started node."); 

        return node;
    }
    
    public Node stop()
    {
        setNodeStatus( node, NodeState.INACTIVE ,OperationType.IDLE, "Successfully stopped Peer. "); 
        
        return node;
    }    
    
    
    private void setNodeStatus( Node node, NodeState state ,OperationType operationType, String message ) 
    {
        if( node != null ) 
        {
            node.setState( state );
            node.getStatus().setOperationType( operationType );
            node.getStatus().setMessage( message );
        }
    }
    
    public NodeStatus getStatus() 
    {
        return node.getStatus();
    }

    @Override
    public Node getNode()
    {
        return node;
    }

}
