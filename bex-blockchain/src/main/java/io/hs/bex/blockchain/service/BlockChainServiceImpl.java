package io.hs.bex.blockchain.service;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.model.FeeRateStack;
import io.hs.bex.blockchain.model.fee.FeeData;
import io.hs.bex.blockchain.model.fee.FeePriorityType;
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


@Service( "BlockChainService" )
public class BlockChainServiceImpl implements BlockChainService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockChainServiceImpl.class );
    // ---------------------------------
    
//    @Value( "${node.ipfs.key.blockchain}" )
//    private String KEY_BLOCKCHAIN;

    final String BLOCKCHAIN_ROOT_FOLDER = "/blockchain";
    final static int RECENT_BLOCKS_COUNT = 10;
    final int FEE_ESTIMATE_PERIOD = 120; // seconds

    private FeeRateStack FEE_RATE_STACK = new FeeRateStack();

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

    private FeeData FEE_DATA = new FeeData();
    private boolean TASKS_ACTIVE = false;

    @PostConstruct
    public void init()
    {
        String handlerName = "";

        for( Node node: nodeService.getNodes() )
        {
            handlerName = node.getProvider().getCurrencyType().getCode() + "-BlockChainHandler";
            BlockChainHandler blockChainHandler = (BlockChainHandler) appContext.getBean( handlerName );
            blockChainHandler.init( node );
            chainHandlers.put( node.getId(), blockChainHandler );
        }

        FEE_DATA.addCoin( DigitalCurrencyType.BTC.getCode(), 1440, 120, 30 );
        FEE_DATA.addCoin( DigitalCurrencyType.BCH.getCode(), 240, 120, 30 );
        FEE_DATA.addCoin( DigitalCurrencyType.ETH.getCode(), 30, 5, 2);
    }

    private BlockChainHandler getHandler( int nodeId )
    {
        return chainHandlers.get( nodeId );
    }

    private BlockChainHandler getHandler( String provider )
    {
        if( Strings.isNullOrEmpty( provider ) )
            return getHandler( DigitalCurrencyType.BTC.getId() );

        if( provider.toLowerCase().equals( "eth" ) )
            return getHandler( DigitalCurrencyType.ETH.getId() );
        else if( provider.toLowerCase().equals( "btc" ) )
            return getHandler( DigitalCurrencyType.BTC.getId() );
        else if( provider.toLowerCase().equals( "btc-testnet" ) )
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.TESTNET.getId() );
        else if( provider.toLowerCase().equals( "btc-regtest" ) )
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.REGTEST.getId() );
        else
            return getHandler( DigitalCurrencyType.BTC.getId() + NodeNetworkType.TESTNET.getId() );
    }

    @Override
    public void startTasks()
    {
        if(!TASKS_ACTIVE)
        {
            taskManager.startScheduledTask( startFeeEstimateTask(), "FeeEstimateTask", 60, FEE_ESTIMATE_PERIOD );
            TASKS_ACTIVE = true;
        }
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
            return getHandler( nodeId ).getEstimatedFee( nBlocks );
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
        BlockChainHandler bcHandler;

        try
        {
            for( Integer id: chainHandlers.keySet() )
            {
                bcHandler = chainHandlers.get( id );

                if( bcHandler.getNode().getProvider().getNetworkType() == NodeNetworkType.MAINNET )
                {
                    FeeRate feeRate = bcHandler.getEstimatedFee( 0 );

                    if( feeRate != null && feeRate.getHighPriorityRate() > 0 )
                    {
                        DigitalCurrencyType ct = bcHandler.getNode().getProvider().getCurrencyType();
                        FEE_RATE_STACK.getRates().put( ct, feeRate );

                        //----------- New format --------------------------------------
                        FEE_DATA.setPriorityData( ct.getCode(), FeePriorityType.LOW , feeRate.getLowPriorityRate() );
                        FEE_DATA.setPriorityData( ct.getCode(), FeePriorityType.MEDIUM , feeRate.getMediumPriorityRate());
                        FEE_DATA.setPriorityData( ct.getCode(), FeePriorityType.HIGH, feeRate.getHighPriorityRate());
                    }
                }
            }

            FEE_RATE_STACK.setTime( Instant.now() );
            saveFile( "/estimatefee", "index.json", mapper.writeValueAsString( FEE_RATE_STACK ) );

            //----------------------------------------------
            FEE_DATA.setTime( Instant.now() );
            saveFile( "/estimatefee", "feerates.json", mapper.writeValueAsString( FEE_DATA ) );

            //-----------------------------------------------
            // dataStoreService.publishNS( KEY_BLOCKCHAIN, "blockchain", "" );
            //-----------------------------------------------
        }
        catch( Exception e )
        {
            logger.error( "(!!!) Error when saving estimate fee", e );
        }
    }

    private void saveFile( String path, String fileName, String value ) throws JsonProcessingException
    {
        dataStoreService.saveFile( true, BLOCKCHAIN_ROOT_FOLDER + path, fileName, value );
    }
}
