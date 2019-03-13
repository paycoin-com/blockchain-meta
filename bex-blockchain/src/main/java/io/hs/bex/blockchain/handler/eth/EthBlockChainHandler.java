package io.hs.bex.blockchain.handler.eth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.blockchain.handler.eth.infura.InfuraHandler;
import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;

@Service("ETH-BlockChainHandler")
@Scope("prototype")
public class EthBlockChainHandler  implements BlockChainHandler
{
    InfuraHandler infuraHandler;
    ObjectMapper objectMapper;
    private Node node;
    
    @Autowired
    private Environment env;
    
    @Autowired
    public EthBlockChainHandler( InfuraHandler infuraHandler, ObjectMapper objectMapper ) 
    {
        this.infuraHandler = infuraHandler;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Node init( Node node )
    {
        String prefix = ""; 
        
        if( node.getProvider().getNetworkType() != NodeNetworkType.MAINNET ) 
            prefix = "-" + node.getProvider().getNetworkType().name().toLowerCase();
                
        String apiUrl = env.getProperty( "node.eth" + prefix + ".api.url" );
        String apiKey = env.getProperty( "node.eth" + prefix + ".api.key" );
        
        infuraHandler.init( apiUrl, apiKey );
        
        this.node = node;
        return node;
    }
    
    @Override
    public FeeRate getEstimatedFee( int nBlocks )
    {
        return infuraHandler.getEstimedFeeRate();
    }


    @Override
    public Node getNode()
    {
        return node;
    }

}
