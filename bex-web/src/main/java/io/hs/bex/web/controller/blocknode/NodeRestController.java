package io.hs.bex.web.controller.blocknode;

import java.util.NoSuchElementException;

import io.hs.bex.blockchain.service.api.BlockChainService;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeStatus;
import io.hs.bex.blocknode.service.api.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping( value = "/node/" )
public class NodeRestController
{
    // ---------------------------------
    private static final Logger LOGGER = LoggerFactory.getLogger( NodeRestController.class );
    // ---------------------------------
    
    @Autowired
    NodeService nodeService;
    
    @Autowired
    BlockChainService blockChainService; 
    
   
    @RequestMapping( value = "/start", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> startNode( @RequestParam( required = false, name = "node_id" ) int nodeId )
    {
        try
        {
            return new ResponseEntity<Node>( nodeService.startNode( nodeId ), 
                    HttpStatus.OK );
        }
        catch ( NoSuchElementException e )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        catch ( Exception e )
        {
            LOGGER.error( " Error starting block node by Id:{}", nodeId, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    @RequestMapping( value = "/stop", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> stopNode( @RequestParam( required = false, name = "node_id" ) int nodeId )
    {
        try
        {
            return new ResponseEntity<Node>( nodeService.startNode( nodeId ), 
                    HttpStatus.OK );
        }
        catch ( NoSuchElementException e )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        catch ( Exception e )
        {
            LOGGER.error( " Error stopping block node by Id:{}", nodeId, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    @RequestMapping( value = "/status/{nodeId}", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getNodeStatus( @PathVariable(  "nodeId" ) int nodeId )
    {
        try
        {
            return new ResponseEntity<NodeStatus>( nodeService.getNodeStatus( nodeId ), 
                    HttpStatus.OK );
        }
        catch ( NoSuchElementException e )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
        catch ( Exception e )
        {
            LOGGER.error( " Error syncing blocks by Id:{}", nodeId, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

}
