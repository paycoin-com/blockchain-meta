package io.hs.bex.blockchain.service.api;

import java.util.List;

import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.blockchain.model.address.GenericAddress;
import io.hs.bex.blockchain.model.tx.GenericTransaction;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.common.model.CollectionTracker;

public interface BlockChainService
{
    List<GenericBlock> getRecentBlocks( String provider );

    GenericBlock getBlockByHash( String provider, String blockHash );

    GenericTransaction getTransactionByHash( String provider, String blockHash, String txHash );

    GenericAddress getAddressDetails( String provider, String address );

    List<GenericBlock> getBlocks( String provider, CollectionTracker<GenericBlock> tracker );

    List<GenericAddress> getAddressDetails( String provider, String[] addresses );

    Node syncBlocks( int nodeId );

    Node syncLocalBlocks( int nodeId );

    double getEstimatedTxFee( String provider );

}
