package io.hs.bex.blockchain.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blockchain.service.api.BlockChainService;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;
import io.hs.bex.blocknode.service.api.NodeService;
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
        String handlerName;
        
        for( Node node:nodeService.getNodes() ) 
        {
            BlockChainHandler bitcoinRegtest = (BlockChainHandler) appContext.getBean( "BitcoinBlockChainHandler" );
            //bitcoinRegtest.init( node.getId() );
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
    public FeeRate getEstimatedFee( String nodeId, int nBlocks )
    {
        try
        {
            return getHandler(nodeId).getEstimatedFee( nBlocks );
        }
        catch( Exception e )
        {
            logger.error( "(E!) Error getting Estimated Fee blocks nodeId:{}", nodeId, e );
            
            return new FeeRate();
        }  
    }
    
}
