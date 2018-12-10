package io.hs.bex.blockchain.handler.btc.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.handler.btc.model.MempoolInfo;
import io.hs.bex.blockchain.model.FeeRate;

public class FeeEstimateUtil
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( FeeEstimateUtil.class );
    // ---------------------------------
    
    private final long EXTRAPOLATION_PERIOD = 20 * 60 * 1000;  // 20 MINS
    
    private BcoinHandler bcoinHandler;
    
    private ObjectMapper mapper;
    
    private MempoolInfo memPool;
    
    
    public FeeEstimateUtil( BcoinHandler bcoinHandler, ObjectMapper mapper )
    {
        this.mapper = mapper;
        this.bcoinHandler = bcoinHandler;
    }
    
    
    public FeeRate getEsimatedFee( int nBlocks ) 
    {
        try 
        {
            memPool = bcoinHandler.getMempoolInfo();
        }
        catch( Exception e ) 
        {
        }
        
        return null;
    }
    
    
}
