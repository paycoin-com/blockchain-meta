package io.hs.bex.blockchain.service.api;

import java.util.List;

import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.blockchain.model.address.GenericAddress;
import io.hs.bex.blockchain.model.tx.GenericTransaction;
import io.hs.bex.blocknode.model.Node;

public interface BlockChainHandler
{
    List<GenericBlock> getRecentBlocks( int recentCount );

    GenericBlock getBlockByHash( String hash );

    GenericTransaction getTransactionByHash( String blockHash, String txHash );

    GenericAddress getAddressDetails( String address );

    List<GenericAddress> getAddressDetails( List<String> addressList );

    Node init( int nodeId );

    Node syncBlocks();

    Node syncLocalBlocks();

}
