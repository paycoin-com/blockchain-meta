package io.hs.bex.blockchain.handler.btc.utils;


import java.time.Instant;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.google.common.collect.Iterables;

import io.hs.bex.blockchain.dao.EstimateFeeRateDAO;
import io.hs.bex.blockchain.dao.FeeRateDataDAO;
import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.FeeEstimateData;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;
import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.model.store.EstimateFeeRate;
import io.hs.bex.blockchain.model.store.EstimateFeeRateDetails;
import io.hs.bex.blockchain.model.store.FeeEstimationData;
import io.hs.bex.blockchain.model.store.PredictedFeeValues;
import io.hs.bex.common.model.DigitalCurrencyType;
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

    private EstimateFeeRate estimateFeeRate = new EstimateFeeRate();

    private EstimateFeeRateDAO estimateFeeRateDAO;
    
    private FeeRateDataDAO feeRateDataDAO;
    

    public FeeEstimateUtil( EstimateFeeRateDAO estimateFeeRateDAO, FeeRateDataDAO feeRateDataDAO,
            BcoinHandler bcoinHandler )
    {
        this.bcoinHandler = bcoinHandler;
        this.estimateFeeRateDAO = estimateFeeRateDAO;
        this.feeRateDataDAO = feeRateDataDAO;
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
        Page<EstimateFeeRate> dataPage = estimateFeeRateDAO.getLatest( PageRequest.of( 0, 1 ) );

        if( dataPage != null && dataPage.hasContent() )
        {
            EstimateFeeRate rate = dataPage.getContent().get( 0 );
            return new FeeRate( rate.getLowPriority(), rate.getMeidumPriority(), rate.getHighPriority() );
        }
        else
            return null;
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

                    // -------save data ------------
                    estimateFeeRate.setCoinId( DigitalCurrencyType.BTC.getId() );
                    estimateFeeRate.setTimestamp( Instant.now() );
                    estimateFeeRateDAO.save( estimateFeeRate );
                    estimateFeeRate = new EstimateFeeRate();
                    // -----------------------------
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
                    feeEstimateData.getCurrSizeData().get( x ).put( time, sum );
                else
                {
                    Map<Long, Integer> dataMap = new LinkedHashMap<Long, Integer>();
                    dataMap.put( time, sum );
                    feeEstimateData.getCurrSizeData().add( dataMap );
                }

                // **** save data ***************************
                estimateFeeRate.addDetails( new EstimateFeeRateDetails( FeeEstimateData.FEE_RANGES[x], time, sum ) );
                // ******************************************

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

    private void predictValues( long predictionPeriod )
    {
        List<Integer> predictedValues = new ArrayList<>();
        Map<Long, Integer> tempDataMapCurrent = new LinkedHashMap<>();

        try
        {
            long x_period = feeEstimateData.getFetchStartTime() + predictionPeriod;
            int extrapValue = 0;
            int b = 0;

            for( Map<Long, Integer> dataMapPrev: feeEstimateData.getPrevSizeData() )
            {
                Map<Long, Integer> dataMapCurrent = feeEstimateData.getCurrSizeData().get( b );
                int diffValue = feeEstimateData.getBlocksSizeDiff().get( b );

                tempDataMapCurrent.clear();

                for( Long key: dataMapCurrent.keySet() )
                {
                    tempDataMapCurrent.put( key, (int) ( diffValue + dataMapCurrent.get( key )) );
                }

                dataMapPrev.putAll( tempDataMapCurrent );

                if( dataMapPrev.size() > 1 )
                {
                    if( x_period > System.currentTimeMillis() )
                        extrapValue = (int) MathUtils.linearRegAsInt( dataMapPrev, x_period );
                    else
                        extrapValue = (int) dataMapPrev.values().toArray()[dataMapPrev.values().size() - 1];

                    predictedValues.add( (int) extrapValue );
                }

                // ******** save data ****************
                estimateFeeRate.addPredcitedValues( new PredictedFeeValues( FeeEstimateData.FEE_RANGES[b], extrapValue,
                        diffValue, predictionPeriod ) );
                // ***********************************

                // logger.info( "Predicted Values Range:{} = {}", String.format(
                // "%04d", FeeEstimateData.FEE_RANGES[a] ),
                // String.format ("%.1f", (float) extarpValue / 1024));

                b++;

            }

            // ----------------------------

            if( predictionPeriod == FeeEstimateData.PREDICTION_PERIOD_20M )
                estimateFeeRate.setMeidumPriority( estimateFee( FeeEstimateData.PREDICTION_PERIOD_20M, predictedValues ) );
            else
            {
                estimateFeeRate
                        .setHighPriority( estimateFee( FeeEstimateData.PREDICTION_PERIOD_60M, predictedValues ) );
                estimateFeeRate.setLowPriority( ecFeeEstimation.getEstimatedFeeRate() );
            }

        }
        catch( Exception e )
        {
            logger.info( "Error in Prediction:", e );
        }

    }

    public long estimateFee( long period, List<Integer> predictedValues )
    {
        List<Integer> newPredictedValues = new ArrayList<>();
        int lastIndex = 0, b = 0;
        long max = 0;
        int fee = 0;

        try
        {
            try 
            {
                String param  = String.valueOf( period/60/1000) + " minutes";
                Object[] responseObj = (Object[]) feeRateDataDAO.estimateRate( param );
                
                if(responseObj != null) 
                {
                    FeeEstimationData data = new FeeEstimationData( (int)responseObj[0], (double)responseObj[1] ); 
                    
                    if(data != null) 
                        estimateFeeRate.addEstimationData( data );
                }
            }
            catch( Exception e ) 
            {
                //ignore
            }
            
            for( Integer predValue: predictedValues )
            {
                int diffValue = feeEstimateData.getBlocksSizeDiff().get( b );
                newPredictedValues.add( predValue - diffValue );

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
            
            logger.info( "Estimating Fee Index|Size: {} | {}", fee,String.format( "%.1f", (float) max / 1024 ) );

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
