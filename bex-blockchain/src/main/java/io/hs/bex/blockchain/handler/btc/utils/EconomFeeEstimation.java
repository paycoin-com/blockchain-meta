package io.hs.bex.blockchain.handler.btc.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.BlockFeeData;
import io.hs.bex.blockchain.handler.btc.model.BlockInfo;
import io.hs.bex.blockchain.handler.btc.model.TxInfo;

public class EconomFeeEstimation
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( EconomFeeEstimation.class );
    // ---------------------------------
    
    private BcoinHandler bcoinHandler;

    private final int DATA_FETCH_PERIOD = 60; // SECONDS
    
    private final int FEE_ORDER_PERCENTAGE = 20; // SECONDS
    
    private int CURRENT_BLOCK_HEIGHT = 0;
    
    private double esimatedFeeRate = 0;
    
    private final int BLOCKS_TO_PROCESS = 10;
    
    private Map<Integer,BlockFeeData> blockFeeDataMap = new LinkedHashMap<>();
    
    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();
    

    public EconomFeeEstimation( BcoinHandler bcoinHandler)
    {
        this.bcoinHandler = bcoinHandler;
        initData();
        
        // ------------------------------------------
        startScheduledTask( 30, DATA_FETCH_PERIOD );
        // ------------------------------------------
    }
    
    private void startScheduledTask( int startAfter, int period )
    {
        try
        {
            timerService.scheduleWithFixedDelay( () -> fethBlockInfo(), startAfter, period, TimeUnit.SECONDS );
        }
        catch( Exception e )
        {
            logger.error( "Error starting scheduled task for Fee Estimate: {}", e.toString() );
        }
    }
    
    private void initData()
    {
        CURRENT_BLOCK_HEIGHT = getLastBlockHeight() - BLOCKS_TO_PROCESS;
    }
    
    private void fethBlockInfo()
    {
        int blockHeight = getLastBlockHeight();
        int currentHeight = CURRENT_BLOCK_HEIGHT;
        
        logger.info( "Getting peer info blockHeight:{} ,currentHeight:{} ", blockHeight, currentHeight );
        
        if( blockHeight != currentHeight) 
        {
            for(  ;blockHeight > currentHeight ; blockHeight -- ) 
            {
                processBlockInfo( bcoinHandler.getBlock( blockHeight ));
                CURRENT_BLOCK_HEIGHT = blockHeight;
            }
        }
    }
    
//    private void truncateFeeData( int maxRecords ) 
//    {
//        if(blockFeeDataMap.size() < maxRecords) 
//        {
//            //blockFeeDataMap. put( block.getHeight(), processFeeInfo( block.getTime(), block.getTxs() )  ); 
//        }
//    }

    
    private void processBlockInfo( BlockInfo block ) 
    {
        logger.info( "Processing Block:{} ", block.getHeight());

        blockFeeDataMap.put( block.getHeight(), processFeeInfo( block.getTime(), block.getTxs() )  ); 
    }
    
    private BlockFeeData processFeeInfo( long timeEpochSec, List<TxInfo> txs ) 
    {
        BlockFeeData blockFeeData = new BlockFeeData( timeEpochSec );
        blockFeeData.setFee( getFeeByPercentage( FEE_ORDER_PERCENTAGE, txs ) );
        
        logger.info( "Processing Fee  --> BlockFeeData:{} ", blockFeeData );
        
        return blockFeeData;
    }
    
    private long getFeeByPercentage( float percentage, List<TxInfo> txs ) 
    {
        try 
        {
            logger.info( "getFeeByPercentage Before clearing:{} ", txs.size() );

            //-------Clear fee == 0 -----------
            txs.removeIf( c -> 0 == c.getFee() );
            Collections.sort( txs );
            
            logger.info( "getFeeByPercentage After clearing:{} ", txs.size() );
            logger.info( "getFeeByPercentage After Sort:{} ", txs );
            
            int index = txs.size();
            
            index = index * FEE_ORDER_PERCENTAGE /100;
            //---------------------------------
            logger.info( "Index:{}, Fee:{} ", index, txs.get( index ).getFee());

            
            return txs.get( index ).getFee();
        }
        catch( Exception e ) 
        {
            //ignore
        }
        
        return 0;
    }
    
    private int getLastBlockHeight() 
    {
        return bcoinHandler.getPeerInfo().getChainInfo().getLastBlockHeight();
    }
    
    public double getEstimatedFeeRate() 
    {
        return esimatedFeeRate;
    }

    public Map<Integer, BlockFeeData> getBlockFeeDataMap()
    {
        return blockFeeDataMap;
    }
    
}
