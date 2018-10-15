package io.hs.bex.blockchain.listener;

import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeState;
import io.hs.bex.blocknode.model.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockStoreEventListener
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockStoreEventListener.class );
    // ---------------------------------
    
    private Node node;
    private float percentage = 0;
    private long blocksLeft = 0;
    
    public BlockStoreEventListener( Node node )
    {
        this.node = node;
    }
    
    public void progress( long blocksCount, long blocksSoFar )
    {
        blocksLeft = blocksSoFar;
        percentage = ((float)(blocksCount - blocksSoFar) * 100 / blocksCount);
        
        if( node != null ) 
        {
            node.getStatus().getOperationProgress().setCompletePercentage( percentage );
            node.getStatus().getOperationProgress().setBlocksLeft( blocksLeft );
        }
        
        if( ( blocksSoFar % 100 ) == 0 ) 
        {
            logger.info( "Local store cynchronization completed % {}.", percentage );
        }
    }
    
    
    protected void doneDownload() 
    {
        if( node != null ) 
        {
            node.setState( NodeState.ACTIVE_SYNC );
            node.getStatus().setMessage( "Local store sync complete !!!" );
            node.getStatus().setOperationType( OperationType.IDLE );
        }
    }
    
    public float getPercentage()
    {
        return percentage;
    }

    public void setPercentage( float percentage )
    {
        this.percentage = percentage;
    }

    public long getBlocksLeft()
    {
        return blocksLeft;
    }

    public void setBlocksLeft( long blocksLeft )
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
