package io.hs.bex.blockchain.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.blockchain.model.address.GenericAddress;
import io.hs.bex.blockchain.model.tx.GenericTransaction;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blockchain.service.api.BlockChainService;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;
import io.hs.bex.blocknode.service.api.NodeService;
import io.hs.bex.common.model.CollectionTracker;
import io.hs.bex.common.model.DigitalCurrencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Service("BlockChainService")
public class BlockChainServiceImpl implements BlockChainService 
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockChainServiceImpl.class );
    // ---------------------------------

    final static int RECENT_BLOCKS_COUNT = 10;
    
    @Autowired
    NodeService nodeService;
        
    @Autowired
    private ApplicationContext appContext;
    
    Map<Integer, BlockChainHandler> chainHandlers = new HashMap<>();
    

    @PostConstruct
    public void init() 
    {
        for( Node node:nodeService.getNodes() ) 
        {
            BlockChainHandler bitcoinRegtest = (BlockChainHandler) appContext.getBean( "BitcoinBlockChainHandler" );
            bitcoinRegtest.init(node.getId() );
            chainHandlers.put( node.getId(), bitcoinRegtest );
        }
    }
    
    private BlockChainHandler getHandler( int nodeId )
    {
        return chainHandlers.get( nodeId );
    }
        
    private BlockChainHandler getHandler( String provider ) 
    {
        if(Strings.isNullOrEmpty( provider ))
            return getHandler( DigitalCurrencyType.BTC.getId() );
        
        if(provider.toLowerCase().equals( "btc" ))
            return getHandler( DigitalCurrencyType.BTC.getId() );
        else if(provider.toLowerCase().equals( "btc-testnet" ) || provider.toLowerCase().equals( "btc-testnet3" ) )
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.TESTNET.getId() );
        else if(provider.toLowerCase().equals( "btc-regtest" ))
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.REGTEST.getId() );
        else
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.TESTNET.getId() );
    }
    
    @Override
    public Node syncBlocks( int nodeId )
    {
        try
        {
            return getHandler(nodeId).syncBlocks();
        }
        catch( Exception e )
        {
            logger.error( "(E!) Error synchronizing blocks nodeId:{}", nodeId, e );
            
            return null;
        }  
    }
    
    @Override
    public Node syncLocalBlocks( int nodeId )
    {
        try
        {
            return getHandler(nodeId).syncLocalBlocks();
        }
        catch( Exception e )
        {
            logger.error( "(E!) Error synchronizing blocks nodeId:{}", nodeId, e );
            
            return null;
        }  
    }
    
    
    @Override
    public GenericBlock getBlockByHash( String provider, String blockHash ) 
    {
        try 
        {
            return getHandler( provider ).getBlockByHash( blockHash );
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting block by Hash: {}", blockHash, e );
            
            return null;
        }
    }
    
    
    @Override
    public GenericTransaction getTransactionByHash( String provider, String blockHash, String txHash ) 
    {
        try 
        {
            return getHandler( provider ).getTransactionByHash( blockHash, txHash );
        }
        catch( Exception e ) 
        {
            logger.error( "Error getting transaction by Hash: {}", txHash, e );

            return null;
        }
    }
    
    @Override
    public List<GenericBlock> getBlocks( String provider, CollectionTracker<GenericBlock> tracker ) 
    {
        try 
        {
            return null;
            
        }
        catch( Exception e ) 
        {
            return Collections.emptyList();
        }
    }

    @Override
    public List<GenericBlock> getRecentBlocks( String provider ) 
    {
        try 
        {
            return getHandler( provider ).getRecentBlocks( RECENT_BLOCKS_COUNT );
        }
        catch( Exception e ) 
        {
            return Collections.emptyList();
        }
    }
    

    @Override
    public GenericAddress getAddressDetails( String provider, String address )
    {
        try 
        {
            return getHandler( provider ).getAddressDetails( address );
        }
        catch( Exception e ) 
        {
            return null;
        }
    }
    
    @Override
    public List<GenericAddress> getAddressDetails( String provider, String[] addresses )
    {
        try 
        {
            return getHandler( provider ).getAddressDetails( Arrays.asList( addresses ));
        }
        catch( Exception e ) 
        {
            return Collections.emptyList();
        }
    }
    
    
    @Override
    public double getEstimatedTxFee( String provider )
    {
        try 
        {
            return getHandler( provider ).getEstimatedTxFee();
        }
        catch( Exception e ) 
        {
            return 0;
        }
    }

    
}
