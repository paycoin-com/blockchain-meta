package io.hs.bex.blockchain.task;

import io.hs.bex.blockchain.service.api.BlockChainService;

public class FeeEstimateTask implements Runnable
{
    private BlockChainService blockChainService;
    
    public FeeEstimateTask( BlockChainService blockChainService )
    {
        this.blockChainService = blockChainService;
    }

    @Override
    public void run()
    {
        blockChainService.saveFeeRates();
    }

}
