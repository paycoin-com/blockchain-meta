package io.hs.bex.blockchain.handler.bitcoin;

import java.sql.SQLException;
import javax.annotation.PostConstruct;

import org.bitcoinj.core.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blockchain.dao.api.BlockChainDAO;
import io.hs.bex.blockchain.model.store.BlockChainData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;


@Service("BtcDBStore")
public class BtcDBStore
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcDBStore.class );
    // ---------------------------------

    
    @Autowired
    BlockChainDAO blockChainDAO;
    

    @PostConstruct
    public void init() 
    {
    }
    
    
    public void store( Node node, long blockHeight, Block block )
    {
        try 
        {
            saveBlockData( (short)node.getId(), block, blockHeight );
        }
        catch( Exception e ) 
        {
            logger.error( "(!!!) Error storing to data node:", e );
        }
    }
    
    
    private void saveBlockData( short nodeId, Block block, long blockHeight ) 
            throws JsonProcessingException, SQLException 
    {
        byte[] data = block.bitcoinSerialize(); 
        
        BlockChainData blockChainData = new BlockChainData( nodeId, block.getHashAsString(), blockHeight , data);

        blockChainDAO.save( blockChainData );
    }

}
