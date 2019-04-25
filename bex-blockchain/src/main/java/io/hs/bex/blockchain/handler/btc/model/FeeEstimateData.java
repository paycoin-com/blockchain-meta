package io.hs.bex.blockchain.handler.btc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.hs.bex.blockchain.model.FeeRate;

public class FeeEstimateData
{
    public static final long PREDICTION_PERIOD_20M = 20 * 60 * 1000;  // 20 MINS
    public static final long PREDICTION_PERIOD_60M = 60 * 60 * 1000;  // 60 MINS
    
//    public static final short PREDICTION_PERIOD_20M = 20;  // 20 MINS
//    public static final short PREDICTION_PERIOD_60M = 60;  // 60 MINS
    
    public static short[] FEE_RANGES = { 
                           1700,1500,  1300, 1200, 1100, 1000, 
                           900,  800,  700,  600, 500, 400, 350, 
                           300,  250,  200,  190, 180, 170, 160, 
                           150,  145,  140,  135, 130, 125, 120, 115, 
                           110,  105,  100,  95,  90,
                             85,  80,  75,   70,  65,  60,  55, 50,   
                             45,  40,   35,  30,  28,   26, 
                             24,  22,   20,  19,  18,   17,   
                             16,  15,   14,  13,  12,   11,     
                             10,   9,    8,   7,   6,    5,     
                              4,   3,    2,   1,   0 };
    
    private long fetchStartTime = 0;
    private long lastMempoolSize = 0;
    
    private List<Map<Long, Integer>> currSizeData = new ArrayList<Map<Long, Integer>>();
    private List<Map<Long, Integer>> prevSizeData = new ArrayList<Map<Long, Integer>>();
    private List<Integer> blocksSizeDiff = new ArrayList<Integer>();
    
    private FeeRate feeRate = new FeeRate( 0, 0, 0 );

    public long getFetchStartTime()
    {
        return fetchStartTime; 
    }

    public void setFetchStartTime( long fetchStartTime )
    {
        this.fetchStartTime = fetchStartTime;
    }

    public long getLastMempoolSize()
    {
        return lastMempoolSize;
    }

    public void setLastMempoolSize( long lastMempoolSize )
    {
        this.lastMempoolSize = lastMempoolSize;
    }
    
    public FeeRate getFeeRate()
    {
        return feeRate;
    }

    public void setFeeRate( FeeRate feeRate )
    {
        this.feeRate = feeRate;
    }

    public List<Map<Long, Integer>> getCurrSizeData()
    {
        return currSizeData;
    }

    public void setCurrSizeData( List<Map<Long, Integer>> currSizeData )
    {
        this.currSizeData = currSizeData;
    }

    public List<Map<Long, Integer>> getPrevSizeData()
    {
        return prevSizeData;
    }

    public void setPrevSizeData( List<Map<Long, Integer>> prevSizeData )
    {
        this.prevSizeData = prevSizeData;
    }

    public List<Integer> getBlocksSizeDiff()
    {
        return blocksSizeDiff;
    }

    public void setBlocksSizeDiff( List<Integer> blocksSizeDiff )
    {
        this.blocksSizeDiff = blocksSizeDiff;
    }
    
    
}
