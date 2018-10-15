package io.hs.bex.blocknode.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.blocknode.handler.bitcoin.BtcNodeHandler;
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

    @PostConstruct
    public void init()
    {
        GenericNodeHandler bitcoinRegtest = (GenericNodeHandler) appContext.getBean( "BitcoinNodeHandler" );
        Node node = bitcoinRegtest.init( new NodeProvider(DigitalCurrencyType.BTC, NodeNetworkType.REGTEST ));
        nodeHandlers.put( node.getId(), bitcoinRegtest );
        
        //GenericNodeHandler bitcoinMainnet = (GenericNodeHandler) appContext.getBean( "BitcoinNodeHandler" );
        //node = bitcoinMainnet.init( new NodeProvider(DigitalCurrencyType.BITCOIN, NodeNetworkType.TESTNET ));
        //nodeHandlers.put( node.getId(), bitcoinTestnet );
        
        GenericNodeHandler bitcoinTestnet = (GenericNodeHandler) appContext.getBean( "BitcoinNodeHandler" );
        node = bitcoinTestnet.init( new NodeProvider(DigitalCurrencyType.BTC, NodeNetworkType.TESTNET ));
        nodeHandlers.put( node.getId(), bitcoinTestnet );
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
