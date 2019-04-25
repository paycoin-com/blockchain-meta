package io.hs.bex.blockchain.handler.btc;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hs.bex.blockchain.dao.EstimateFeeRateDAO;
import io.hs.bex.blockchain.dao.FeeRateDataDAO;
import io.hs.bex.blockchain.handler.btc.utils.FeeEstimateUtil;
import io.hs.bex.blockchain.model.FeeRate;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;

@Service("BTC-BlockChainHandler")
@Scope("prototype")
@Transactional
public class BtcBlockChainHandler implements BlockChainHandler
{
    BcoinHandler bcoinHandler;
    FeeEstimateUtil feeEstimateUtil;
    ObjectMapper objectMapper;
    private Node node;
    
    @Autowired
    private Environment env;
    
    @Autowired
    FeeRateDataDAO feeRateDataDAO;
    
    @Autowired
    EstimateFeeRateDAO estimatefeeRateDAO;
    
    @Autowired
    public BtcBlockChainHandler( BcoinHandler bcoinHandler, ObjectMapper objectMapper ) 
    {
        this.bcoinHandler = bcoinHandler;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Node init( Node node )
    {
        String prefix = ""; 
        
        if( node.getProvider().getNetworkType() != NodeNetworkType.MAINNET ) 
            prefix = "-" + node.getProvider().getNetworkType().name().toLowerCase();
                
        String apiUrl = env.getProperty( "node.btc" + prefix + ".api.url" );
        bcoinHandler.init( apiUrl );
        
        if( node.getProvider().getNetworkType() == NodeNetworkType.MAINNET ) 
        {
            this.feeEstimateUtil = new FeeEstimateUtil( estimatefeeRateDAO, feeRateDataDAO, bcoinHandler );
        }
        
        this.node = node;
        return node;
    }
    
    @Override
    public FeeRate getEstimatedFee( int nBlocks )
    {
        if(node.getNetwork().getType() == NodeNetworkType.MAINNET )
            return feeEstimateUtil.getEsimatedFee( nBlocks );
        else
            return bcoinHandler.getEstimedFeeRate();
    }

    @Override
    public Node getNode()
    {
        return node;
    }

}
