package io.hs.bex.web.controller.blocknode;

import io.hs.bex.blockchain.service.api.BlockChainService;
import io.hs.bex.blocknode.service.api.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.hs.bex.web.view.ModelView;


@Controller
public class NodeController
{
    @SuppressWarnings( "unused" )
    private static final Logger logger = LoggerFactory.getLogger( NodeController.class );
    
    @Autowired
    NodeService nodeService;
    
    @Autowired
    BlockChainService blockChainService; 
    
    @RequestMapping(value={ "/blockchain-tasks-start"}, method = RequestMethod.GET)
    public String currencyStartSync( ModelMap model ) 
    {
        blockChainService.startTasks();
        
        return blockNodeListView( model );
    }
    
    @RequestMapping(value={ "/node-list"}, method = RequestMethod.GET)
    public String blockNodeListView( ModelMap model ) 
    {
        model.addAttribute( "nodes", nodeService.getNodes());
        return ModelView.VIEW_NODE_LIST_PAGE;
    }
    
    @RequestMapping(value={ "/node-details"}, method = RequestMethod.GET)
    public String blockNodeDetailsView( ModelMap model, @RequestParam( name="node_id" ) int nodeId ) 
    {
        model.addAttribute( "node", nodeService.getNode( nodeId ));
        
        return ModelView.VIEW_NODE_DETAILS_PAGE;
    }

    @RequestMapping(value={ "/node-action"}, method = RequestMethod.GET)
    public String blockNodeDetailsView( ModelMap model, @RequestParam( name="node_id" ) int nodeId,
            @RequestParam( name="action_type" ) int actionType) 
    {
        if(actionType == 1)
            model.addAttribute( "node", nodeService.startNode( nodeId ));
        else if(actionType == 2)
            model.addAttribute( "node", nodeService.stopNode( nodeId ));
//        else if(actionType == 3)
//            model.addAttribute( "node", blockChainService.syncBlocks( nodeId ));
//        else if(actionType == 4)
//            model.addAttribute( "node", blockChainService.syncLocalBlocks( nodeId ));
//        
        return ModelView.VIEW_NODE_DETAILS_PAGE;
    }
}
