package io.hs.bex.blockchain.handler.btc.utils;


import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import io.hs.bex.blockchain.dao.FeeRateDataDAO;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.BlockInfo;
import io.hs.bex.blockchain.handler.btc.model.TxInfo;
import io.hs.bex.blockchain.model.store.FeeRateData;


public class EconomFeeEstimation
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( EconomFeeEstimation.class );
    // ---------------------------------

    private BcoinHandler bcoinHandler;

    private FeeRateDataDAO feeRateDataDAO;

    private final int DATA_FETCH_PERIOD = 120; // SECONDS    
    private final int DATA_CALCULATE_TASK_PERIOD = 600; // SECONDS

    private final long OVERALL_BLOCK_PROC_PERIOD = 10 * 60 * 60; // HOURS
    // private final long BLOCK_PROC_PERIOD = 1 * 60;// HOURS
    private final int FEE_ORDER_PERCENTAGE_10 = 10;
    private final int FEE_ORDER_PERCENTAGE_20 = 20;
    private final int FEE_ORDER_PERCENTAGE_40 = 40;

    private long FULL_FETCH_RANGE = 0;

    private long LAST_BLOCK_HEIGHT = 0;

    private long esimatedFeeRate = 1;

    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();

    public EconomFeeEstimation( FeeRateDataDAO feeRateDataDAO, BcoinHandler bcoinHandler )
    {
        this.bcoinHandler = bcoinHandler;
        this.feeRateDataDAO = feeRateDataDAO;

        initData();

        // ------------------------------------------
        startInfoFetchTask( 50, DATA_FETCH_PERIOD );
        startCalculationTask( 180, DATA_CALCULATE_TASK_PERIOD );
        // ------------------------------------------
    }

    private void startInfoFetchTask( int startAfter, int period )
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
    
    private void startCalculationTask( int startAfter, int period )
    {
        try
        {
            timerService.scheduleWithFixedDelay( () -> calculateRate(), startAfter, period, TimeUnit.SECONDS );
        }
        catch( Exception e )
        {
            logger.error( "Error starting calculation task for Fee Estimate: {}", e.toString() );
        }
    }


    private void initData()
    {
        try 
        {
            Page<FeeRateData> feeData = feeRateDataDAO.getLatest( PageRequest.of( 0, 1 ));

            if( feeData == null || !feeData.hasContent() )
                FULL_FETCH_RANGE = ( System.currentTimeMillis() / 1000) - OVERALL_BLOCK_PROC_PERIOD;
            else
                LAST_BLOCK_HEIGHT = feeData.getContent().get( 0 ).getHeight();
            
        }
        catch( Exception e ) 
        {
            logger.error( "Error initializing data from DB :", e );
        }
    }
    
    private void calculateRate() 
    {
        try 
        {
            Long rate = feeRateDataDAO.calculateRate();
            
            if( rate != null && rate.longValue() > 0) 
            {
                logger.info( "Estimated ECONOM Fee :{} S/Byte", rate);
                esimatedFeeRate = (long) Math.ceil( (float)rate/1024);
                
                if(esimatedFeeRate < 1)
                    esimatedFeeRate = 1;
            }
        }
        catch( Exception e ) 
        {
            logger.error( "Error running calculation task :", e );
        }
    }

    private void fethBlockInfo()
    {
        long value = 0;
        long fetchUntil = 0;
        int blockHeight = getLastBlockHeight();
        int startHeight = blockHeight;

        if( LAST_BLOCK_HEIGHT != blockHeight )
        {
            BlockInfo blockInfo = null;

            logger.info( "Getting peer info blockHeight:{}", blockHeight );

            if( FULL_FETCH_RANGE != 0 ) 
            {
                fetchUntil = FULL_FETCH_RANGE;
                value = System.currentTimeMillis() / 1000;
            }
            else 
            {
                fetchUntil = LAST_BLOCK_HEIGHT;
                value = blockHeight;
            }

            for( ; fetchUntil < value; blockHeight-- )
            {
                blockInfo = bcoinHandler.getBlock( blockHeight );
                processBlockInfo( blockInfo );

                if( FULL_FETCH_RANGE != 0 )
                    value = blockInfo.getTime();
                else
                    value = blockInfo.getHeight();
            }

            FULL_FETCH_RANGE = 0;
            LAST_BLOCK_HEIGHT = startHeight;
        }
    }

    private void saveFeeData( FeeRateData feeData )
    {
        if( feeData != null )
            feeRateDataDAO.save( feeData );
    }

    private void processBlockInfo( BlockInfo block )
    {
        logger.info( "Processing Block:{} ", block.getHeight() );
        
        try 
        {
            saveFeeData( processFeeInfo( block.getHeight(), block.getTime(), block.getTxs() ) );
        }
        catch( Exception e ) 
        {
            logger.error( "Error processing block Info:", e );
        }

    }

    private FeeRateData processFeeInfo( long height, long timeEpochSec, List<TxInfo> txs )
    {
        FeeRateData feeRateData = getFeeByPercentage( txs );
        
        if(feeRateData != null) 
        {
            feeRateData.setHeight( height );
            feeRateData.setDate( Instant.ofEpochSecond( timeEpochSec ) );
        }
        
        return feeRateData;
    }

    private FeeRateData getFeeByPercentage( List<TxInfo> txs )
    {
        try
        {
            FeeRateData feeRateData = new FeeRateData();

            //logger.info( "getFeeByPercentage Before clearing:{} ", txs.size() );

            // -------Clear fee == 0 -----------
            txs.removeIf( c -> 0 == c.getFeeRate() );
            Collections.sort( txs );

            int index_10 = txs.size() * FEE_ORDER_PERCENTAGE_10 / 100;
            int index_20 = txs.size() * FEE_ORDER_PERCENTAGE_20 / 100;
            int index_40 = txs.size() * FEE_ORDER_PERCENTAGE_40 / 100;
            // ---------------------------------
            feeRateData.setValue1( txs.get( index_10 ).getFeeRate() );
            feeRateData.setValue2( txs.get( index_20 ).getFeeRate() );
            feeRateData.setValue3( txs.get( index_40 ).getFeeRate() );

            return feeRateData;
        }
        catch( Exception e )
        {
            // ignore
        }

        return null;
    }

    private int getLastBlockHeight()
    {
        return bcoinHandler.getPeerInfo().getChainInfo().getLastBlockHeight();
    }

    public long getEstimatedFeeRate()
    {
        return esimatedFeeRate;
    }

}
