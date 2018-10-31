package io.hs.bex.blockchain.service.api;

import io.hs.bex.blockchain.listener.BlockStoreEventListener;
import io.hs.bex.blocknode.model.Node;

public interface BlockStoreService
{
    BlockStoreEventListener getBlockStoreEventListener();

    void setBlockStoreEventListener( BlockStoreEventListener blockStoreEventListener );

    void store( Node node, long blockHeight, byte[] blockObject );
}
