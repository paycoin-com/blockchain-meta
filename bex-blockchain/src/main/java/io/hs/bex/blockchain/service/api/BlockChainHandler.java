package io.hs.bex.blockchain.service.api;

import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blocknode.model.Node;

public interface BlockChainHandler
{
    FeeRate getEstimatedFee( int nBlocks );

    Node init( Node node );

    Node getNode();
}
