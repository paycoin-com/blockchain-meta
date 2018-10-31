package io.hs.bex.blockchain.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.bitcoinj.store.BlockStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import io.hs.bex.blockchain.dao.api.BlockChainDAO;
import io.hs.bex.blockchain.listener.BlockStoreEventListener;
import io.hs.bex.blockchain.model.store.BlockChainData;
import io.hs.bex.blockchain.service.api.BlockStoreService;
import io.hs.bex.blocknode.model.Node;

@Service
public class BlockChainStoreService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockChainStoreService.class );
    // ---------------------------------
    
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

    @Async
    @Transactional
    public void syncData( Node node )
    {
        Page<BlockChainData> blockChainDataPage = null;
        
        List<Long> blockIds = new ArrayList<>();
        
        if( blockStoreEventListener == null )
            blockStoreEventListener = new BlockStoreEventListener( node );
        
        int blocksCount = 0;
        int blocksLeft  = 0;
        
        try 
        {
            Pageable pageable = PageRequest.of( 0, 25 );
            
            do 
            {
                blockChainDataPage = blockChainDAO.findByStatus( (short)0, (short)node.getId(), pageable );
                
                if(blockChainDataPage.hasContent()) 
                {
                    blocksCount = (int)blockChainDataPage.getTotalElements();
                    blocksLeft = blocksCount - (blockChainDataPage.getSize() * blockChainDataPage.getNumber());
                    
                    logger.info( "Fetching Blocks for NodeId:{}  - BlocksLeft:{}",node.getId(), blocksLeft );
                    
                    for( BlockChainData blockData : blockChainDataPage.getContent() ) 
                    {
                        blockStoreService.store( node, blockData.getHeight(), blockData.getData() );
                        blockIds.add( blockData.getId() );
                        
                        blockStoreEventListener.progress( blocksCount , --blocksCount  );
                    }
                    
                    Long [] ids = blockIds.stream().toArray(Long[]::new);
                    blockChainDAO.updateStatus( ids , (short)1 );
                    blockIds.clear();
                }
                
                pageable = blockChainDataPage.nextPageable();
            }
            while( blockChainDataPage.hasNext() );
            
        }
        catch( Exception e ) 
        {
            logger.error( "(!) Error starting local sync", e  );
        }
    }
}
