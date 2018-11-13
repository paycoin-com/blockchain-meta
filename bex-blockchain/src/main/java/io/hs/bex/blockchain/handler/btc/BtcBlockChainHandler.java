package io.hs.bex.blockchain.handler.btc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;

@Service("BtcBlockChainHandler")
@Scope("prototype")
public class BtcBlockChainHandler implements BlockChainHandler
{
    private FeeEstimationService feeEstimationService;
    
    @Autowired
    public BtcBlockChainHandler() 
    {
        
    }
    
    @Override
    public FeeRate getEstimatedFee( int nBlocks )
    {
        return null;
    }

}
