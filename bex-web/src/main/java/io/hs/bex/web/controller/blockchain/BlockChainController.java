package io.hs.bex.web.controller.blockchain;


import io.hs.bex.blockchain.service.api.BlockChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.hs.bex.web.view.ModelView;

@Controller
public class BlockChainController
{
    @SuppressWarnings( "unused" )
    private static final Logger logger = LoggerFactory.getLogger( BlockChainController.class );

    @Autowired
    BlockChainService blockChainService;
 
//    @RequestMapping( value = { "/block-list" }, method = RequestMethod.GET )
//    public String blockNodeListView( ModelMap model, @RequestParam(name="provider", required = false) String provider )
//    {
//        model.addAttribute( "provider", provider );
//        model.addAttribute( "blocks", blockChainService.getRecentBlocks( provider ) );
//        return ModelView.VIEW_BLOCK_LIST_PAGE;
//    }
//
//    @RequestMapping( value = { "/block-details" }, method = RequestMethod.GET )
//    public String blockNodeDetailsView( ModelMap model,@RequestParam(name="provider", required = false) String provider,
//                                                       @RequestParam( name = "hash" ) String blockHash )
//    {
//        model.addAttribute( "provider", provider );
//        model.addAttribute( "block", blockChainService.getBlockByHash( provider, blockHash ));
//
//        return ModelView.VIEW_BLOCK_DETAILS_PAGE;
//    }
    
//    @RequestMapping( value = { "/blocks/{blockHash}" }, method = RequestMethod.GET )
//    public String blockNodeDetailsViewExt( ModelMap model, @RequestParam(name="provider", required = false) String provider,
//                                                           @PathVariable( "blockHash" ) String blockHash )
//    {
//        return blockNodeDetailsView(model, provider, blockHash );
//    }
//
//    @RequestMapping( value = { "/txs/{txHash}" }, method = RequestMethod.GET )
//    public String txDetailsViewExt( ModelMap model, @PathVariable( "txHash" ) String txHash,
//                                                    @RequestParam( name = "blockhash" ) String blockHash,
//                                                    @RequestParam(name="provider", required = false) String provider)
//    {
//        model.addAttribute( "provider", provider );
//        model.addAttribute( "transaction", blockChainService.getTransactionByHash( provider, blockHash, txHash ));
//        
//        return ModelView.VIEW_TX_DETAILS_PAGE;
//    }
//    
//    @RequestMapping( value = { "/address/{address}" }, method = RequestMethod.GET )
//    public String addressDetailsViewExt( ModelMap model, @PathVariable( "address" ) String address,
//                                                    @RequestParam(name="provider", required = false) String provider)
//    {
//        model.addAttribute( "provider", provider );
//        model.addAttribute( "address", blockChainService.getAddressDetails( provider, address ));
//        
//        return ModelView.VIEW_ADDRESS_DETAILS_PAGE;
//    }
    
    

}
