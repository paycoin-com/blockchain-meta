package io.hs.bex.blockchain.handler.bitcoin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.hs.bex.blocknode.model.Node;
import io.hs.bex.common.utils.StringUtils;
import io.hs.bex.datastore.model.DataStoreType;
import io.hs.bex.datastore.service.api.DataStoreService;
import io.hs.bex.blockchain.handler.bitcoin.dao.BlockChainDAO;
import io.hs.bex.blockchain.listener.BlockStoreEventListener;
import io.hs.bex.blockchain.model.store.AddressData;
import io.hs.bex.blockchain.model.store.BlockData;
import io.hs.bex.blockchain.model.store.TxData;
import io.hs.bex.blockchain.model.store.TxInputData;
import io.hs.bex.blockchain.model.store.TxOutputData;
import io.hs.bex.blockchain.service.api.BlockStoreService;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Service("BtcFileSystemStore")
public class BtcFileSystemStore implements BlockStoreService
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcFileSystemStore.class );
    // ---------------------------------

    static final String BLOCKSTORE_ROOT_FOLDER = "/blockstore/";

    @Autowired
    DataStoreService dataStoreService;
    
    @Autowired
    BlockChainDAO blockChainDAO;
    
    private ObjectMapper mapper  = new ObjectMapper();
    
    private BlockStoreEventListener blockStoreEventListener;
    
    @SuppressWarnings( "deprecation" )
    @Override
    public void store( Node node, long blockHeight, Object blockObject )
    {
        //BlockStoreData blockStoreData = null;
        Block block = (Block) blockObject;
        AddressData addressData = null;
        
        try 
        {
            String nodeDataPath = BLOCKSTORE_ROOT_FOLDER +
                    node.getProvider().getCurrencyType().getCode() + "/" 
                   + node.getNetwork().getType().name().toLowerCase();

            Map<String, AddressData> addressDataMap = new HashMap<>();
    
            BlockData blockData = new BlockData();
            blockData.setHash( block.getHashAsString() );
            blockData.setMerkleRoot( block.getMerkleRoot().toString() );
            blockData.setNonce( block.getNonce() );
            blockData.setTimeSeconds( block.getTimeSeconds() );
            blockData.setHeight( blockHeight );
                    
            List<Transaction> transactions = block.getTransactions();
                    
            for( Transaction tx : transactions ) 
            {
                TxData txData = new TxData();
                txData.setHash( tx.getHashAsString() );
                txData.setBlockHash( block.getHashAsString() );
                blockData.getTransactions().add( txData.getHash() );
    
                for( TransactionInput txInput: tx.getInputs() )
                {
                    TxInputData txInputData = new TxInputData();
                    if( !txInput.isCoinBase() )
                    {
                        if( txInput.getOutpoint() != null )
                        {
                            txInputData.setHash( txInput.getOutpoint().getHash().toString() );
                            txInputData.setIndex( txInput.getOutpoint().getIndex() );
                        }
    
                        try
                        {
                            txInputData.setAddress( txInput.getFromAddress().toString() );
                        }
                        catch( Exception e )
                        { /* ignore */ }
                    }
    
                    if( !Strings.isNullOrEmpty( txInputData.getAddress() ) )
                    {
                        if( addressDataMap.containsKey( txInputData.getAddress() ) )
                            addressData = addressDataMap.get( txInputData.getAddress() );
                        else
                        {
                            addressData = new AddressData();
                            addressDataMap.put( txInputData.getAddress(), addressData );
                        }
                        addressData.setAddress( txInputData.getAddress() );
                        addressData.addBlockData( blockData.getHash(), blockData.getHeight() );
                    }
    
                    txData.getInputs().add( txInputData );
                }
    
                for( TransactionOutput txOutput: tx.getOutputs() )
                {
                    TxOutputData txOutputData = new TxOutputData();
    
                    double value = txOutput.getValue() != null ? 
                            ( (double) txOutput.getValue().value / Coin.COIN.value): 0;
                    txOutputData.setIndex( txOutput.getIndex() );
                    txOutputData.setHash( txOutput.getOutPointFor().getHash().toString() );
                    txOutputData.setValue( value );
    
                    try
                    {
                        if( txOutput.getScriptPubKey() != null && !Strings.isNullOrEmpty(
                                txOutput.getScriptPubKey().getFromAddress( txOutput.getParams() ).toString() ) )
                        {
                            txOutputData.setAddress(
                                    txOutput.getScriptPubKey().getFromAddress( txOutput.getParams() ).toString() );
                        }
                    
                        if( Strings.isNullOrEmpty( txOutputData.getAddress() ) )
                        {
                            Optional<Address> p2pKHAddr = Optional
                                    .ofNullable( txOutput.getAddressFromP2PKHScript( txOutput.getParams() ) );
                            Optional<Address> p2pHr = Optional
                                    .ofNullable( txOutput.getAddressFromP2SH( txOutput.getParams() ) );
        
                            if( p2pKHAddr.isPresent() )
                                txOutputData.setAddress( p2pKHAddr.get().toString() );
                            else if( p2pHr.isPresent() )
                                txOutputData.setAddress( p2pHr.get().toString() );
                        }
                    
                    }
                    catch( Exception e )
                    { /* ignore */ }
    
                    if( !Strings.isNullOrEmpty( txOutputData.getAddress() ) )
                    {
                        if( addressDataMap.containsKey( txOutputData.getAddress() ) )
                            addressData = addressDataMap.get( txOutputData.getAddress() );
                        else
                        {
                            addressData = new AddressData();
                            addressDataMap.put( txOutputData.getAddress(), addressData );
                        }
                        addressData.setAddress( txOutputData.getAddress() );
                        addressData.addBlockData( blockData.getHash(), blockData.getHeight() );
                    }
    
                    txData.getOutputs().add( txOutputData );
                }
    
                //saveTxData( network, txData );
            }
                    
            saveAddressaData( nodeDataPath, addressDataMap ); 
            //saveBlockData( network, blockData, blockStoreData );
        }
        catch( Exception e ) 
        {
            logger.error( "(!!!) Error storing to data to FS:", e );
        }
    }
    
    private void saveFile( String path, String data ) 
    {
        dataStoreService.saveFile( DataStoreType.FILESYSTEM, true, path, "index.json", data );
    }
    
    private String getFileContent( String path ) 
    {
        return dataStoreService.getFileContent(DataStoreType.FILESYSTEM, path, "index.json" );
    }
    
//    private void saveBlockData( String nodeDataPath, String hash, BlockData blockData ) 
//            throws JsonProcessingException, SQLException 
//    {
//        String blockDir = nodeDataPath + "/blocks" + StringUtils.createDirStructure( hash, 3, 2 );
//        saveFile( blockDir, "index.json", mapper.writeValueAsString( blockData ) );
//    }
//    
//    private void saveTxData( String nodeDataPath, String hash, TxData txData )  throws JsonProcessingException 
//    {
//        String txDir =  nodeDataPath + "/txs" + StringUtils.createDirStructure( hash, 3, 2 );
//        saveFile( txDir, "index.json", mapper.writeValueAsString( txData ) );
//    }
    
    private synchronized void saveAddressaData( String nodeDataPath, Map<String, AddressData> addressDataMap ) 
            throws IOException 
    {
        for( String address : addressDataMap.keySet()) 
        {
            String addrDir =  nodeDataPath + "/address" + StringUtils.createDirStructure( address, 3,2 );
            String payload = getFileContent( addrDir );
            
            AddressData addressData = addressDataMap.get( address );
            String data = "";
            
            if( !Strings.isNullOrEmpty( payload ) ) 
            {
                AddressData exAddressData = mapper.readValue( payload, AddressData.class );
                exAddressData.getBlocks().addAll( addressData.getBlocks() );
                addressData = exAddressData;
                data = mapper.writeValueAsString( addressData );
                saveFile( addrDir, data ) ;
            }
            else 
            {
                data = mapper.writeValueAsString( addressData );
                saveFile( addrDir, data ) ;
            }
        }
    }


    @Override
    public BlockStoreEventListener getBlockStoreEventListener()
    {
        return blockStoreEventListener;
    }

    @Override
    public void setBlockStoreEventListener( BlockStoreEventListener blockStoreEventListener )
    {
        this.blockStoreEventListener = blockStoreEventListener;
    }
    
          
}
