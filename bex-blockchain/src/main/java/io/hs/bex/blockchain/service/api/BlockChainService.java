package io.hs.bex.blockchain.service.api;

import io.hs.bex.blockchain.model.FeeRate;

public interface BlockChainService
{
    FeeRate getEstimatedFee( String provider, int nBlocks );
}
