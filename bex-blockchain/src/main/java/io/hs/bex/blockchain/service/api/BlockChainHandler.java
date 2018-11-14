package io.hs.bex.blockchain.service.api;

import io.hs.bex.blockchain.model.FeeRate;

public interface BlockChainHandler
{
    FeeRate getEstimatedFee( int nBlocks );
}
