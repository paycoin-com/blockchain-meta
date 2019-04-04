package io.hs.bex.blockchain.handler.btc.utils;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import io.hs.bex.blockchain.dao.FeeRateDataDAO;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.FeeEstimateData;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;
import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.common.utils.MathUtils;


public class FeeEstimateUtil
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( FeeEstimateUtil.class );
    // ---------------------------------
    
    private final int DATA_FETCH_PERIOD = 35; // SECONDS
    private final int BLOCK_SIZE = 1048576; // in bytes (1MB)

    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();

    private BcoinHandler bcoinHandler;
    
    private FeeEstimateData feeEstimateData;

    private EconomFeeEstimation ecFeeEstimation;

    public FeeEstimateUtil( FeeRateDataDAO feeRateDataDAO, BcoinHandler bcoinHandler )
    {
        this.bcoinHandler = bcoinHandler;
        this.feeEstimateData = new FeeEstimateData();
        this.ecFeeEstimation = new EconomFeeEstimation( feeRateDataDAO, bcoinHandler );

        // ------------------------------------------
        startScheduledTask( 30, DATA_FETCH_PERIOD );
        // ------------------------------------------
    }

    public FeeEstimateUtil( BcoinHandler bcoinHandler, int dataFetchPeriod )
    {
        this.bcoinHandler = bcoinHandler;
        this.feeEstimateData = new FeeEstimateData();

        // ------------------------------------------
        if( dataFetchPeriod > 0 )
            startScheduledTask( 0, dataFetchPeriod );
        // ------------------------------------------
    }

    public FeeEstimateUtil( BcoinHandler bcoinHandler, FeeEstimateData feeEstimateData, int dataFetchPeriod )
    {
        this.bcoinHandler = bcoinHandler;
        this.feeEstimateData = feeEstimateData;

        // ------------------------------------------
        if( dataFetchPeriod > 0 )
            startScheduledTask( 0, dataFetchPeriod );
        // ------------------------------------------
    }

    public FeeRate getEsimatedFee( int nBlocks )
    {
        return feeEstimateData.getFeeRate();
    }

    private void startScheduledTask( int startAfter, int period )
    {
        try
        {
            timerService.scheduleWithFixedDelay( () -> fetchMempoolStats(), startAfter, period, TimeUnit.SECONDS );
        }
        catch( Exception e )
        {
            logger.error( "Error starting scheduled task for Fee Estimate: {}", e.toString() );
        }

    }

    private void fetchMempoolStats()
    {
        MempoolInfo memPool = bcoinHandler.getMempoolInfo();
        logger.info( "*** Fetching mempoolInfo Size:{} *** ", memPool.getSize() );

        if( memPool.getSize() < feeEstimateData.getLastMempoolSize() )
        {
            feeEstimateData.setFetchStartTime( System.currentTimeMillis() );

            feeEstimateData.getPrevSizeData().clear();
            feeEstimateData.getPrevSizeData().addAll( feeEstimateData.getCurrSizeData() );
            feeEstimateData.getCurrSizeData().clear();

            logger.info( "*** Block event detected !!! Starting fetching elements *** " );

            setStatsData();
        }
        else
        {
            if( feeEstimateData.getFetchStartTime() != 0 )
            {
                setStatsData();

                if( feeEstimateData.getPrevSizeData().size() > 0 )
                {
                    predictValues( FeeEstimateData.PREDICTION_PERIOD_20M );
                    predictValues( FeeEstimateData.PREDICTION_PERIOD_60M );
                }
            }
        }

        feeEstimateData.setLastMempoolSize( memPool.getSize() );
    }

    private void setStatsData()
    {
        int maxValue = 0;
        int minValue = 0;
        int sum = 0;

        long time = System.currentTimeMillis();

        try
        {
            // ------------- fetch mempool TX ----------------------------
            List<MempoolTx> memPoolTxs = bcoinHandler.getMempoolTxs( 1 );
            // -----------------------------------------------------------

            for( int x = 0; x < FeeEstimateData.FEE_RANGES.length; x++ )
            {
                maxValue = FeeEstimateData.FEE_RANGES[x];

                if( ( x + 1) == FeeEstimateData.FEE_RANGES.length )
                    minValue = FeeEstimateData.FEE_RANGES[x] * 1024;
                else
                    minValue = FeeEstimateData.FEE_RANGES[x + 1];

                sum = (int) getFilteredSum( memPoolTxs, minValue, maxValue );

                if( feeEstimateData.getCurrSizeData().size() > x )
                {
                    feeEstimateData.getCurrSizeData().get( x ).put( time, sum );
                }
                else
                {
                    Map<Long, Integer> dataMap = new LinkedHashMap<Long, Integer>();
                    dataMap.put( time, (int) sum );
                    feeEstimateData.getCurrSizeData().add( dataMap );
                }

                // logger.info( "Tx Sum Values Range:{} = {}", String.format(
                // "%04d", FeeEstimateData.FEE_RANGES[x] ),
                // String.format ("%.1f", (double) sum / 1024) );

            }

            logger.info( "Size of the CURRENT_BLOCK: {}", feeEstimateData.getCurrSizeData().get( 0 ).size() );

            if( feeEstimateData.getCurrSizeData().get( 0 ).size() == 1 && feeEstimateData.getPrevSizeData().size() > 0 )
            {
                setBlockSizeDiff();
            }
        }
        catch( Exception e )
        {
            logger.info( "Error in Setting Diff:", e );
        }

    }

    private void setBlockSizeDiff()
    {
        try
        {
            feeEstimateData.getBlocksSizeDiff().clear();

            List<Integer> lastPrevData = new ArrayList<>();
            List<Integer> lastCurrentData = new ArrayList<>();

            for( Map<Long, Integer> prevData: feeEstimateData.getPrevSizeData() )
            {
                Entry<Long, Integer> lastEntry = Iterables.getLast( prevData.entrySet() );
                lastPrevData.add( lastEntry.getValue() );
            }
            for( Map<Long, Integer> prevData: feeEstimateData.getCurrSizeData() )
            {
                Entry<Long, Integer> lastEntry = Iterables.getLast( prevData.entrySet() );
                lastCurrentData.add( lastEntry.getValue() );
            }

            int i = 0;

            for( int prevValue: lastPrevData )
            {
                int diff = prevValue - lastCurrentData.get( i );

                if( diff < 0 )
                    diff = 0;

                feeEstimateData.getBlocksSizeDiff().add( diff );
                // logger.info( "Diff Values Range:{} , Size: {}",
                // String.format( "%04d", FeeEstimateData.FEE_RANGES[i] ),
                // String.format ("%.1f", (double) diff / 1024) );
                i++;
            }

        }
        catch( Exception e )
        {

        }
    }

    private void predictValues( long predictionPreriod )
    {
        List<Integer> predictedValues = new ArrayList<>();
        Map<Long, Integer> tempDataMapCurrent = new LinkedHashMap<>();

        try
        {
            long x_period = feeEstimateData.getFetchStartTime() + predictionPreriod;
            int extarpValue = 0;
            int b = 0;

            for( Map<Long, Integer> dataMapPrev: feeEstimateData.getPrevSizeData() )
            {
                Map<Long, Integer> dataMapCurrent = feeEstimateData.getCurrSizeData().get( b );
                double diffValue = feeEstimateData.getBlocksSizeDiff().get( b );

                tempDataMapCurrent.clear();

                for( Long key: dataMapCurrent.keySet() )
                {
                    tempDataMapCurrent.put( key, (int) ( diffValue + dataMapCurrent.get( key )) );
                }

                dataMapPrev.putAll( tempDataMapCurrent );

                if( dataMapPrev.size() > 1 )
                {
                    if( x_period > System.currentTimeMillis() )
                        extarpValue = (int) MathUtils.linearRegAsInt( dataMapPrev, x_period );
                    else
                        extarpValue = (int) dataMapPrev.values().toArray()[dataMapPrev.values().size() - 1];

                    predictedValues.add( (int) extarpValue );
                }

                // logger.info( "Predicted Values Range:{} = {}", String.format(
                // "%04d", FeeEstimateData.FEE_RANGES[a] ),
                // String.format ("%.1f", (float) extarpValue / 1024));

                b++;

            }

            // ----------------------------

            if( predictionPreriod == FeeEstimateData.PREDICTION_PERIOD_20M )
                feeEstimateData.getFeeRate().setMediumPriorityRate( estimateFee( predictedValues ) );
            else
            {
                feeEstimateData.getFeeRate().setHighPriorityRate( estimateFee( predictedValues ) );
                feeEstimateData.getFeeRate().setLowPriorityRate( ecFeeEstimation.getEstimatedFeeRate() );
            }
            // ----------------------------
        }
        catch( Exception e )
        {
            logger.info( "Error in Prediction:", e );
        }

    }

    public long estimateFee( List<Integer> predictedValues )
    {
        List<Integer> newPredictedValues = new ArrayList<>();
        int lastIndex = 0, b = 0;
        long max = 0;
        FeeRate feeRate = null;
        long fee = 0;

        try
        {
            for( Integer predValue: predictedValues )
            {
                int diffValue = feeEstimateData.getBlocksSizeDiff().get( b );
                newPredictedValues.add( predValue - diffValue );

//                logger.info( "Pred. Range|Diff|Actual:{} | {} | {} | {}",
//                        String.format( "%04d", FeeEstimateData.FEE_RANGES[b] ),
//                        String.format( "%6.2f", (float) predValue / 1024 ),
//                        String.format( "%6.2f", (float) diffValue / 1024 ),
//                        String.format( "%6.2f", (float) ( predValue - diffValue) / 1024 ) );

                b++;
            }

            for( int x = 0; x < newPredictedValues.size(); x++ )
            {
                max += newPredictedValues.get( x );

                if( max >= BLOCK_SIZE )
                    break;

                lastIndex = x;
            }

            fee = FeeEstimateData.FEE_RANGES[lastIndex];
            // feeRate = new FeeRate( highFee / 5, highFee, highFee * 2 );

            logger.info( "Estimating Fee Index|Size|FeeRate:{} | {} | {}", lastIndex,
                    String.format( "%.1f", (float) max / 1024 ), feeRate );
        }
        catch( Exception e )
        {
            logger.info( "Error Estimating Fee", e );
        }

        return fee;
    }

    private double getFilteredSum( List<MempoolTx> memPoolTxs, double minValue, double maxValue )
    {
        return memPoolTxs.stream().filter( o -> ( minValue < o.getFeeRate() && o.getFeeRate() <= maxValue) )
                .mapToDouble( o -> o.getSize() ).sum();
    }

}
