package io.hs.bex.blockchain.handler.btc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;

@Service("BTC-BlockChainHandler")
@Scope("prototype")
public class BtcBlockChainHandler implements BlockChainHandler
{
    @Autowired
    private BcoinHandler bcoinHandler;
    
    @Autowired
    private Environment env;
    
    private Node node;
    
    @Override
    public Node init( Node node )
    {
        String prefix = ""; 
        
        if( node.getProvider().getNetworkType() != NodeNetworkType.MAINNET)
            prefix = "-" + node.getProvider().getNetworkType().name().toLowerCase();
                
        String apiUrl = env.getProperty( "node.btc" + prefix + ".api.url" );
        bcoinHandler.init( apiUrl );
        
        this.node = node;
        return node;
    }
    
    @Override
    public FeeRate getEstimatedFee( int nBlocks )
    {
        return bcoinHandler.getEstimedFeeRate();
    }

    @Override
    public Node getNode()
    {
        return node;
    }

}