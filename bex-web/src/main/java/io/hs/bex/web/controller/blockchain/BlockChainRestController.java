package io.hs.bex.web.controller.blockchain;


import java.util.List;

import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.blockchain.model.address.GenericAddress;
import io.hs.bex.blockchain.model.tx.GenericTransaction;
import io.hs.bex.blockchain.service.api.BlockChainService;
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

import com.google.common.base.Strings;

@RestController
@RequestMapping( value = "/api/v1/blockchain/" )
public class BlockChainRestController
{
    private static final Logger logger = LoggerFactory.getLogger( BlockChainRestController.class );

    @Autowired
    BlockChainService blockChainService;
    
    @RequestMapping( value = "{provider}/address/{address}", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getAddressDetails( 
            @PathVariable( name = "provider" ) String provider, @PathVariable( name = "address" ) String address)
    {
        try
        {        
            if(Strings.isNullOrEmpty( provider ))
                provider = "BTC";

            logger.info( " GET /{}/address/{}" , provider, address);
            return new ResponseEntity<GenericAddress>( 
                    blockChainService.getAddressDetails( provider, address ) , HttpStatus.OK);
        }
        catch ( Exception e )
        {
            logger.error( " Error getting address details for address:{}" + address, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    
    @RequestMapping( value = "{provider}/address", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getAddressesDetails( 
            @PathVariable( name = "provider" ) String provider,
            @RequestParam( name = "ads" , required = false ) String[] ads, 
            @RequestParam( name = "ads[]" , required = false ) String[] aAds )
    {
        try
        {        
            if(Strings.isNullOrEmpty( provider ))
                provider = "BTC";

            logger.info( " GET /{}/address?ads={}" , provider, ads);
            return new ResponseEntity<List<GenericAddress>>( 
                    blockChainService.getAddressDetails( provider, (ads == null) ? aAds : ads ), HttpStatus.OK);
        }
        catch ( Exception e )
        {
            logger.error( " Error getting address details for address:{}" + aAds, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    @RequestMapping( value = "{provider}/blocks/{hash}", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getBlockDetails( 
            @PathVariable( name = "provider" ) String provider, @PathVariable( name = "hash" ) String blockHash)
    {
        try
        {        
            if(Strings.isNullOrEmpty( provider ))
                provider = "BTC";

            logger.info( " GET /{}/blocks/{}" , provider, blockHash);
            return new ResponseEntity<GenericBlock>( 
                    blockChainService.getBlockByHash( provider, blockHash ) , HttpStatus.OK);
        }
        catch ( Exception e )
        {
            logger.error( " Error getting block details for hash:" + blockHash, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
    
    
    @RequestMapping( value = "{provider}/blocks/{blockHash}/txs/{txHash}", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getTxDetails( 
            @PathVariable( name = "provider" ) String provider, 
            @PathVariable( name = "blockHash" ) String blockHash,
            @PathVariable( name = "txHash" ) String txHash)
    {
        try
        {        
            if(Strings.isNullOrEmpty( provider ))
                provider = "BTC";

            logger.info( " GET /{}/blocks/{}/txs/{}" , provider,blockHash, txHash);
            return new ResponseEntity<GenericTransaction>( 
                    blockChainService.getTransactionByHash( provider, blockHash, txHash ), HttpStatus.OK);
        }
        catch ( Exception e )
        {
            logger.error( " Error getting tx details for hash:{}" + txHash, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }

    @RequestMapping( value = "{provider}/fee/estimate", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> getEstimateTxFee( 
            @PathVariable( name = "provider" ) String provider )
    {
        try
        {        
            if(Strings.isNullOrEmpty( provider ))
                provider = "BTC";

            logger.info( " GET {}/fee/" , provider);
            return new ResponseEntity<Double>( blockChainService.getEstimatedTxFee( provider ), HttpStatus.OK); 
        }
        catch ( Exception e )
        {
            //logger.error( " Error getting tx details for hash:{}" + txHash, e );
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }


}
