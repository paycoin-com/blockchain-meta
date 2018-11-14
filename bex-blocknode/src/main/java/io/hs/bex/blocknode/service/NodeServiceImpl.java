package io.hs.bex.blocknode.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.blocknode.handler.btc.BtcNodeHandler;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;
import io.hs.bex.blocknode.model.NodeProvider;
import io.hs.bex.blocknode.model.NodeStatus;
import io.hs.bex.blocknode.service.api.GenericNodeHandler;
import io.hs.bex.blocknode.service.api.NodeService;
import io.hs.bex.common.model.DigitalCurrencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service( "BlockNodeService" )
public class NodeServiceImpl implements NodeService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcNodeHandler.class );
    // ---------------------------------

    @Autowired
    private ApplicationContext appContext;
    
    private Map<Integer,GenericNodeHandler> nodeHandlers = new HashMap<>();
    
    @Autowired
    private Environment env;

    @PostConstruct
    public void init()
    {
        if(0 != Integer.parseInt(env.getProperty( "node.btc.status.active" ))) 
            initNode( new NodeProvider(DigitalCurrencyType.BTC, NodeNetworkType.MAINNET ));
        
        if(0 != Integer.parseInt(env.getProperty( "node.btc-regtest.status.active" ))) 
            initNode( new NodeProvider(DigitalCurrencyType.BTC, NodeNetworkType.REGTEST ));
        
        if(0 != Integer.parseInt(env.getProperty( "node.btc-testnet.status.active" ))) 
            initNode( new NodeProvider(DigitalCurrencyType.BTC, NodeNetworkType.TESTNET ));
        
        if(0 != Integer.parseInt(env.getProperty( "node.bch.status.active" ))) 
            initNode( new NodeProvider(DigitalCurrencyType.BCH, NodeNetworkType.MAINNET ));
        
        if(0 != Integer.parseInt(env.getProperty( "node.bch-regtest.status.active" ))) 
            initNode( new NodeProvider(DigitalCurrencyType.BCH, NodeNetworkType.REGTEST ));
        
        if(0 != Integer.parseInt(env.getProperty( "node.bch-testnet.status.active" ))) 
            initNode( new NodeProvider(DigitalCurrencyType.BCH, NodeNetworkType.TESTNET ));

    }
    
    private void initNode( NodeProvider nodeProvider ) 
    {
        Node node = null;
        String code = nodeProvider.getCurrencyType().getCode();
        
        GenericNodeHandler nodeHandler = (GenericNodeHandler) appContext.getBean( code + "-NodeHandler" );
        node = nodeHandler.init( new NodeProvider( nodeProvider.getCurrencyType() ,nodeProvider.getNetworkType() ));
        nodeHandlers.put( node.getId(), nodeHandler );
    }
    

    @Override
    public List<Node> getNodes()
    {
        List<Node> nodes = new ArrayList<>();
        
        for( GenericNodeHandler nodeHandler: nodeHandlers.values() ) 
        {
            nodes.add( nodeHandler.getNode() );
        }
        return nodes;
    }
    

    @Override
    public Node getNode( int nodeId )
    {
        return nodeHandlers.get( nodeId ).getNode();
    }
    
    private GenericNodeHandler getNodeHandller( int nodeId )
    {
        return nodeHandlers.get( nodeId );
    }
    
    @Override
    public Node getNode( NodeProvider  nodeProvider )
    {
        return nodeHandlers.get( (int) nodeProvider.getId()  ).getNode();
    }
    
    
    @Override
    public Node startNode( int nodeId )
    {
        try
        {
            return  getNodeHandller(nodeId).start();
        }
        catch( Exception e )
        {
            logger.error( "(E!) Error starting nodeId:{}", nodeId, e );
            
            return null;
        }  
    }
    
    @Override
    public Node stopNode( int nodeId )
    {
        try
        {
            return  getNodeHandller(nodeId).stop();
        }
        catch( Exception e )
        {
            logger.error( "(E!) Error starting nodeId:{}", nodeId, e );
            
            return null;
        }  
    }
    
    @Override
    public NodeStatus getNodeStatus( int nodeId )
    {
        try
        {
            return  getNodeHandller(nodeId).getStatus();
        }
        catch( Exception e )
        {
            logger.error( "(E!) Error getting node status nodeId:{}", nodeId, e );
            return null;
        }  
    }

}
