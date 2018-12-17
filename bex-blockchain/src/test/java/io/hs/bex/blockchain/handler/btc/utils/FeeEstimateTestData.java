package io.hs.bex.blockchain.handler.btc.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.hs.bex.blockchain.handler.btc.model.FeeEstimateData;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.handler.btc.model.MempoolTx;


public class FeeEstimateTestData
{
    private MempoolInfo mempool;
    private List<MempoolTx> memPoolTxs;
    
    private static int requestCount = 0;

    double testValues[] = { 
            0, 0.21875, 0, 1.470703125, 0.8798828125, 0.1875, 2.711914063, 0.8828125, 1.069335938, 1.0703125,
            7.638671875, 4.118164063, 0.4189453125, 3.149414063, 4.23046875, 9.319335938, 56.81640625, 12.97851563,
            19.38085938, 8.905273438, 6.59375, 5.471679688, 20.90429688, 9.090820313, 8.102539063, 84.82617188, 16.50097656,
            3.700195313, 2.94140625, 4.95703125, 5.020507813, 1.934570313, 0, 0.7373046875, 5.728515625, 2.354492188,
            16.46972656, 58.85546875, 28.04882813, 14.38671875, 87.00683594, 48.45898438, 112.671875, 269.5371094,
            400.4550781, 291.6054688, 248.0566406, 1114.06543, 4.620117188, 0, 0 };

    public FeeEstimateTestData()
    {
        mempool = new MempoolInfo();
        memPoolTxs = new ArrayList<>();
    }

    public MempoolInfo getMemPoolInfo()
    {
        requestCount ++;
        
        return getMempoolData();
    }
    
    private MempoolInfo getMempoolData()
    {
        if( requestCount == 4 ) 
        {
            initMempoolValues();
            initMempoolTxValues();
            requestCount = 0;
        }
        else 
        {
            mempool.setSize( mempool.getSize() + 100 );
        }
        
        
        return mempool;
    }
    
    
    public List<MempoolTx> getMempoolTxData()
    {
        int i = 0;
        for( MempoolTx tx: memPoolTxs) 
        {
            double size = tx.getSize();
            
            if( size > 0 ) 
            { 
                size += generateRandom( size, size*2 );
                tx.setSize( size );
                tx.setFeeRate( FeeEstimateData.FEE_RANGES[i] );
            }
            i++;
        }
        
        return memPoolTxs;
    } 
    
    
    private void initMempoolValues() 
    {
        mempool.setSize( 0 );
    }
    
    private void initMempoolTxValues() 
    {
        int x = 0 ;
        memPoolTxs.clear();
        
        for( double range: FeeEstimateData.FEE_RANGES) 
        {
            double fee =  range - 0.5;
            
            MempoolTx mempoolTx = new MempoolTx( "", testValues[x], fee, System.currentTimeMillis()) ;
            mempoolTx.setFeeRate( range );
            memPoolTxs.add( mempoolTx );
            x++;
        }
    }
    
    private double generateRandom( double low, double high ) 
    {
        try 
        {
            Random r = new Random();
            double randNumber = low + (high - low) * r.nextDouble();        
            
            return randNumber;
        }
        catch( Exception e ) 
        {
            System.out.println( "Error in Generate random:" + e.toString() );
            return 0;
        }
    }
}
