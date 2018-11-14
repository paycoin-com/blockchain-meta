package io.hs.bex.blockchain.handler.bch;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.hs.bex.blockchain.handler.btc.BcoinHandler;
import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;


@Service( "BCH-BlockChainHandler" )
@Scope( "prototype" )
public class BchBlockChainHandler implements BlockChainHandler
{
    @Autowired
    private BcoinHandler bcashHandler;

    @Autowired
    private Environment env;

    private Node node;

    @Override
    public Node init( Node node )
    {
        String prefix = "";

        if( node.getProvider().getNetworkType() != NodeNetworkType.MAINNET )
            prefix = "-" + node.getProvider().getNetworkType().name().toLowerCase();

        String apiUrl = env.getProperty( "node.bch" + prefix + ".api.url" );
        bcashHandler.init( apiUrl );

        this.node = node;
        return node;
    }

    @Override
    public FeeRate getEstimatedFee( int nBlocks )
    {
        return bcashHandler.getEstimedFeeRate();
    }

    @Override
    public Node getNode()
    {
        return node;
    }

}
