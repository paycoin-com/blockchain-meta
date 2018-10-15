package io.hs.bex.blockchain.handler.bitcoin.listener;

import java.util.Date;
import javax.annotation.Nullable;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.FilteredBlock;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import io.hs.bex.blockchain.service.api.BlockStoreService;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeState;
import io.hs.bex.blocknode.model.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("BtcBlockChainEventListener")
@Lazy
public class BtcBlockChainListener extends DownloadProgressTracker
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcBlockChainListener.class );
    // ---------------------------------
    
    @Autowired
    @Qualifier( "BtcFileSystemStore" )
    BlockStoreService blockStoreService;
    
    private Node node;
    private float percentage = 0;
    private int blocksLeft = 0;
    
    public BtcBlockChainListener( Node node )
    {
        this.node = node;
    }
    
    @Override
    protected void progress( double pct, int blocksSoFar, Date date )
    {
        super.progress( pct, blocksSoFar, date );
        
        percentage = (float) pct;
        blocksLeft = blocksSoFar;
        
        if(node != null) 
        {
            node.getStatus().getOperationProgress().setCompletePercentage( percentage );
            node.getStatus().getOperationProgress().setBlocksLeft( blocksLeft );
        }

    }
    
    @Override
    protected void doneDownload() 
    {
        if( node != null ) 
        {
            node.setState( NodeState.ACTIVE_SYNC );
            node.getStatus().setMessage( "Download completed !!!" );
            node.getStatus().setOperationType( OperationType.IDLE );
        }
    }
    
    @Override
    public void onBlocksDownloaded( Peer peer, Block block, @Nullable FilteredBlock filteredBlock, int blocksLeft ) 
    {
        this.blocksLeft = blocksLeft;
        
        if(node != null) 
        {
            node.getStatus().getOperationProgress().setCompletePercentage( percentage );
            node.getStatus().getOperationProgress().setBlocksLeft( blocksLeft );
        }
        
        //----Sync Data to LocalStore --------------
        try
        {
            blockStoreService.store( node, peer.getBestHeight(), block );
            //logger.info( "(!) Data saved successfully BlocksLeft:{}  ", blocksLeft );
        }
        catch( Exception e )
        {
            logger.error( "(!!!) Error storing to data node:", e );
        }
        //------------------------------------------
        
        super.onBlocksDownloaded( peer , block, filteredBlock, blocksLeft );
    }

    
    public float getPercentage()
    {
        return percentage;
    }

    public void setPercentage( float percentage )
    {
        this.percentage = percentage;
    }

    public int getBlocksLeft()
    {
        return blocksLeft;
    }

    public void setBlocksLeft( int blocksLeft )
    {
        this.blocksLeft = blocksLeft;
    }

    public Node getNode()
    {
        return node;
    }

    public void setNode( Node node )
    {
        this.node = node;
    }
    
   
}
