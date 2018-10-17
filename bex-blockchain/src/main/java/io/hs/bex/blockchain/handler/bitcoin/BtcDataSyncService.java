package io.hs.bex.blockchain.handler.bitcoin;

import java.util.concurrent.Future;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.hs.bex.blockchain.handler.bitcoin.dao.BlockChainDAO;
import io.hs.bex.blockchain.listener.BlockStoreEventListener;
import io.hs.bex.blockchain.model.LocalStoreStatus;
import io.hs.bex.blockchain.service.api.BlockStoreService;
import io.hs.bex.blocknode.model.Node;

@Service("BtcDataSyncService")
public class BtcDataSyncService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcDataSyncService.class );
    // ---------------------------------
    
    private int MAX_SYNC_JOB = 10000;

    
    @Autowired
    BlockChainDAO blockChainDAO;
    
    @Autowired
    @Qualifier( "BtcFileSystemStore" )
    BlockStoreService blockStoreService;
    
    BlockStore blockStore;
    
    private BlockStoreEventListener blockStoreEventListener;
    
    public BlockStoreEventListener getBlockStoreEventListener()
    {
        return blockStoreEventListener;
    }

    public void setBlockStoreEventListener( BlockStoreEventListener blockStoreEventListener )
    {
        this.blockStoreEventListener = blockStoreEventListener;
    }

    public void syncData( Node node )
    {
        String lastHash = "";
        
        if( blockStoreEventListener == null )
            blockStoreEventListener = new BlockStoreEventListener( node );
        
        blockStoreEventListener.setBlocksLeft( 10000 );
        
        try 
        {
            LocalStoreStatus storeStatus = blockChainDAO.getLocalStoreStatus();
            blockStore = ((BlockChain)node.getBlockChain()).getBlockStore();
            
            if(storeStatus == null) 
            {
                lastHash = blockStore.getChainHead().getHeader().getHashAsString();
                storeStatus = new LocalStoreStatus( 0, 0, 0, lastHash );
                blockChainDAO.saveLocalStoreStatus(storeStatus );
            }
            else 
            {
                lastHash = storeStatus.getBlockHash();
            }
            
            StoredBlock storedBlock = blockStore.get( Sha256Hash.wrap( lastHash ) );
            
            Peer peer = ((PeerGroup)node.getPeerGroup()).getDownloadPeer();
            
            if( storedBlock != null ) 
            {
                for(int x=0;x <= MAX_SYNC_JOB;x++) 
                {
                    Future<Block> fBlock = peer.getBlock( storedBlock.getHeader().getHash() );
                    
                    Block block = fBlock.get();
                    
                    if(block != null) 
                    {
                        blockStoreService.store( node, storedBlock.getHeight(), block );
                        storeStatus.setBlockHash( block.getHashAsString() );
                        
                        if((x%10) == 0)
                            blockChainDAO.updateLocalStoreStatus( storeStatus );
                        
                        blockStoreEventListener.progress( MAX_SYNC_JOB , MAX_SYNC_JOB - x );
                    }
                } 
            }
            else 
            {
                logger.info( "(!) Error starting local sync for Hash:{}, block not found!!! ",lastHash );
            }
        }
        catch( Exception e ) 
        {
            logger.error( "(!) Error starting local sync for Hash:{}",lastHash, e  );
        }
    }
}
