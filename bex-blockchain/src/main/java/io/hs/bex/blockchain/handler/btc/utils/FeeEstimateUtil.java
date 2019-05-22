package io.hs.bex.blockchain.handler.btc.utils;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import io.hs.bex.blockchain.model.store.PredictedFeeValues;
import io.hs.bex.common.model.DigitalCurrencyType;


public class FeeEstimateUtil
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( FeeEstimateUtil.class );
    // ---------------------------------

    private final int DATA_FETCH_PERIOD = 35; // SECONDS

    private final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();

    private BcoinHandler bcoinHandler;

    private FeeEstimateData feeEstimateData;

    private EconomFeeEstimation ecFeeEstimation;

    private EstimateFeeRate estimateFeeRate;

    private EstimateFeeRateDAO estimateFeeRateDAO;

    private FeeRateDataDAO feeRateDataDAO;

    private Instant dataTimeRange = Instant.now();

    public FeeEstimateUtil( EstimateFeeRateDAO estimateFeeRateDAO, FeeRateDataDAO feeRateDataDAO,
            BcoinHandler bcoinHandler )
    {
        this.bcoinHandler = bcoinHandler;
        this.estimateFeeRateDAO = estimateFeeRateDAO;
        this.feeRateDataDAO = feeRateDataDAO;
        this.feeEstimateData = new FeeEstimateData();
        this.ecFeeEstimation = new EconomFeeEstimation( feeRateDataDAO, bcoinHandler );

        initEstimateFeeRate();

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

    public FeeRate getEstimatedFee( int nBlocks )
    {
        Page<EstimateFeeRate> dataPage = estimateFeeRateDAO.getLatest( DigitalCurrencyType.BTC.getId(),
                PageRequest.of( 0, 1 ) );

        if( dataPage != null && dataPage.hasContent() )
        {
            EstimateFeeRate rate = dataPage.getContent().get( 0 );
            return new FeeRate( rate.getLowPriority(), rate.getMeidumPriority(), rate.getHighPriority() );
        }
        else
            return null;
    }

    public void initEstimateFeeRate()
    {
        Page<EstimateFeeRate> dataPage = estimateFeeRateDAO.getLatest( DigitalCurrencyType.BTC.getId(),
                PageRequest.of( 0, 1 ) );

        if( dataPage != null && dataPage.hasContent() )
        {
            EstimateFeeRate rate = dataPage.getContent().get( 0 );
            estimateFeeRate = new EstimateFeeRate( rate );
        }
        else
            estimateFeeRate = new EstimateFeeRate( 1, 0, 0 );
    }

    private void startScheduledTask( int startAfter, int period )
    {
        try
        {
            timerService.scheduleWithFixedDelay( () -> fetchMempoolInfo(), startAfter, period, TimeUnit.SECONDS );
        }
        catch( Exception e )
        {
            logger.error( "Error starting scheduled task for Fee Estimate: {}", e.toString() );
        }

    }

    private void fetchMempoolInfo()
    {
        try
        {
            MempoolInfo memPool = bcoinHandler.getMempoolInfo();
            logger.info( "*** Fetching mempoolInfo Size:{} *** ", memPool.getSize() );

            if( memPool.getSize() < feeEstimateData.getLastMempoolSize() )
            {
                feeEstimateData.setFetchStartTime( System.currentTimeMillis() );

                feeEstimateData.getPrevSizeData().clear();
                feeEstimateData.getPrevSizeData().addAll( feeEstimateData.getCurrSizeData() );
                feeEstimateData.getCurrSizeData().clear();

                logger.info( "*** Block event detected !!! Start fetching elements *** " );

                //fetchTxInfo();
            }
            
            if( feeEstimateData.getFetchStartTime() != 0 )
            {
                fetchTxInfo();

                if( feeEstimateData.getPrevSizeData().size() > 0 )
                {
                    predictValues( FeeEstimateData.PREDICTION_PERIOD_20M );
                    predictValues( FeeEstimateData.PREDICTION_PERIOD_60M );

                    // -------save data ------------
                    estimateFeeRate.setCoinId( DigitalCurrencyType.BTC.getId() );
                    estimateFeeRate.setTimestamp( Instant.now() );

                    estimateFeeRateDAO.save( estimateFeeRate );
                    estimateFeeRate = new EstimateFeeRate( estimateFeeRate );
                    // -----------------------------
                }
            }

            feeEstimateData.setLastMempoolSize( memPool.getSize() );
        }
        catch( Exception e )
        {
            logger.error( "Error fetching mempool data (will retry):", e );
        }
    }

    private void fetchTxInfo()
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

                if( maxValue > 0 )
                {
                    if( ( x + 1) == FeeEstimateData.FEE_RANGES.length )
                        minValue = 0;
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
                    estimateFeeRate.addDetails( new EstimateFeeRateDetails( FeeEstimateData.FEE_RANGES[x], time, sum ));
                    // ******************************************
                }
            }

            logger.info( "Size of the CURRENT_BLOCK: {}", feeEstimateData.getCurrSizeData().get( 0 ).size() );

            if( feeEstimateData.getCurrSizeData().get( 0 ).size() == 1 && feeEstimateData.getPrevSizeData().size() > 0)
            {
                setBlockSizeDiff();
            }
        }
        catch( Exception e )
        {
            logger.error( "Error fetching tx info (will retry):", e );
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

                i++;
            }

        }
        catch( Exception e )
        {
            logger.error( "Error in calculating diff values (ignoring):", e );
        }
    }

    private void predictValues( long predictionPeriod )
    {
        try
        {
            int b = 0;

            for( int diffValue: feeEstimateData.getBlocksSizeDiff() )
            {
                // ******** save data ****************
                estimateFeeRate.addPredcitedValues(
                        new PredictedFeeValues( FeeEstimateData.FEE_RANGES[b], 0, diffValue, predictionPeriod ) );
                // ***********************************
                b++;

            }

            if( predictionPeriod == FeeEstimateData.PREDICTION_PERIOD_20M )
            {
                long fee = estimateFee( FeeEstimateData.PREDICTION_PERIOD_20M, null );

                if( fee > 0 )
                    estimateFeeRate.setMeidumPriority( fee );
            }
            else
            {
                long fee = estimateFee( FeeEstimateData.PREDICTION_PERIOD_60M, null );

                if( fee > 0 )
                    estimateFeeRate.setHighPriority( fee );

                // ----------------------------------------------
                fee = ecFeeEstimation.getEstimatedFeeRate();

                if( estimateFeeRate.getMeidumPriority() > fee )
                    estimateFeeRate.setLowPriority( fee );
                // ----------------------------------------------
            }

        }
        catch( Exception e )
        {
            logger.info( "Error in Prediction:", e );
        }

    }

    public long estimateFee( long period, List<Integer> predictedValues )
    {
        int fee = 0;

        if( !checkTimeRange() )
            return 0;

        try
        {
            Integer responseInt = null;

            if( period == FeeEstimateData.PREDICTION_PERIOD_20M )
                responseInt = feeRateDataDAO.estimateMediumRate();
            else
                responseInt = feeRateDataDAO.estimateHighRate();

            if( responseInt != null )
            {
                fee = responseInt;
                logger.info( "Estimating Priority|Fee: {}|{} ", period / 60 / 1000, fee );
            }
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

    private boolean checkTimeRange()
    {
        if( dataTimeRange.plus( 25, ChronoUnit.MINUTES ).isAfter( Instant.now() ) )
            return false;
        else
            return true;
    }

}
