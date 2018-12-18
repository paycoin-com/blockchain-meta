package io.hs.bex.blockchain.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blockchain.service.api.BlockChainService;
import io.hs.bex.blockchain.task.FeeEstimateTask;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;
import io.hs.bex.blocknode.service.api.NodeService;
import io.hs.bex.common.model.DigitalCurrencyType;
import io.hs.bex.datastore.service.api.DataStoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Service("BlockChainService")
public class BlockChainServiceImpl implements BlockChainService 
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockChainServiceImpl.class );
    // ---------------------------------
    
    final String BLOCKCHAIN_ROOT_FOLDER = "/blockchain";
    final static int RECENT_BLOCKS_COUNT = 10;
    final int FEE_ESTIMATE_PERIOD = 120; //seconds
    
    @Autowired
    BlockChainTaskManager taskManager;
    
    @Autowired
    DataStoreService dataStoreService;
    
    @Autowired
    ObjectMapper mapper;
    
    @Autowired
    NodeService nodeService;
        
    @Autowired
    private ApplicationContext appContext;
    
    Map<Integer, BlockChainHandler> chainHandlers = new HashMap<>();
    
    @PostConstruct
    public void init() 
    {
        String handlerName = "";
        
        for( Node node:nodeService.getNodes() ) 
        {
            handlerName = node.getProvider().getCurrencyType().getCode() +  "-BlockChainHandler";
            BlockChainHandler blockChainHanlder = (BlockChainHandler) appContext.getBean( handlerName );
            blockChainHanlder.init( node );
            chainHandlers.put( node.getId(), blockChainHanlder );
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
        else if(provider.toLowerCase().equals( "btc-testnet" ) )
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.TESTNET.getId() );
        else if(provider.toLowerCase().equals( "btc-regtest" ))
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.REGTEST.getId() );
        else
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.TESTNET.getId() );
    }
    
    @Override
    public void startTasks()
    {
        taskManager.startScheduledTask( startFeeEstimateTask(), "FeeEstimateTask", 60, FEE_ESTIMATE_PERIOD );
    }
    
    private FeeEstimateTask startFeeEstimateTask() 
    {
        return new FeeEstimateTask( this ); 
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

    @Override
    public void saveFeeRates()
    {
        String feeEstimeteRoot;
        BlockChainHandler bcHandler = null;
        
        try 
        {
            for( Integer id : chainHandlers.keySet() ) 
            {
                bcHandler = chainHandlers.get( id );
                feeEstimeteRoot = "/" + bcHandler.getNode().getProvider().getCurrencyType().getCode();
                            
                if(bcHandler.getNode().getProvider().getNetworkType() != NodeNetworkType.MAINNET)
                    feeEstimeteRoot += "/" + bcHandler.getNode().getProvider().getNetworkType().getName();
                
                saveFile( feeEstimeteRoot + "/estimatefee", "index.json", 
                        mapper.writeValueAsString( bcHandler.getEstimatedFee(0) ) );
            }
        }
        catch( Exception e )
        {
            logger.error( "(!!!) Error when saving estimate fee", e );
        }
    }
    
    private void saveFile( String path, String fileName, String value ) throws JsonProcessingException 
    {
        dataStoreService.saveFile( true , BLOCKCHAIN_ROOT_FOLDER + path, fileName, value );
    }
}
