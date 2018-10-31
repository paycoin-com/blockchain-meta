package io.hs.bex.blockchain.handler.bitcoin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.MemoryPoolMessage;
import org.bitcoinj.core.Message;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Ping;
import org.bitcoinj.core.Pong;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.TxConfidenceTable;
import org.bitcoinj.core.listeners.PreMessageReceivedEventListener;

import io.hs.bex.blockchain.handler.bitcoin.listener.BtcBlockChainListener;
import io.hs.bex.blockchain.model.GenericBlock;
import io.hs.bex.blockchain.model.address.GenericAddress;
import io.hs.bex.blockchain.model.tx.GenericTransaction;
import io.hs.bex.blockchain.model.tx.GenericTxInput;
import io.hs.bex.blockchain.model.tx.GenericTxOutput;
import io.hs.bex.blockchain.model.tx.TransactionStatus;
import io.hs.bex.blockchain.service.BlockChainStoreService;
import io.hs.bex.blockchain.service.api.BlockChainHandler;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeState;
import io.hs.bex.blocknode.model.OperationType;
import io.hs.bex.blocknode.service.api.NodeService;
import io.hs.bex.common.model.DigitalCurrencyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service( "BitcoinBlockChainHandler" )
@Scope("prototype")
public class BtcBlockChainHandler implements BlockChainHandler
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcBlockChainHandler.class );
    // ---------------------------------

    @Autowired
    NodeService nodeService;
    
    @Autowired
    private ApplicationContext context;
    
    DigitalCurrencyType BITCOIN = DigitalCurrencyType.BTC; 

    private PeerGroup peerGroup;

    private Node node;
    
    private BtcBlockChainListener blockChainEventListener;
    
    @Autowired
    BlockChainStoreService blockStoreService;

    @Override
    public Node init( int nodeId )
    {
        node = nodeService.getNode( nodeId );
        
        return node;
    }
    
    private Peer getConnectedPeer() 
    {
        if( peerGroup == null )
            peerGroup = (PeerGroup)node.getPeerGroup();
        
        return peerGroup.getDownloadPeer();
    }
    
    
    @Override
    public Node syncBlocks() 
    {
        if( node.getState() == NodeState.ACTIVE_NOTSYNC || node.getState() == NodeState.ACTIVE_SYNC ) 
        {
            peerGroup = (PeerGroup) node.getPeerGroup();
            
            if( peerGroup.isRunning()) 
            {
                node.getStatus().setOperationType( OperationType.SYNC_CORE );
                node.getStatus().setMessage( "Started Block synchronization !!!" );
                
                if(blockChainEventListener == null) 
                    blockChainEventListener=(BtcBlockChainListener)context.getBean( "BtcBlockChainEventListener",node);
    
                peerGroup.startBlockChainDownload( blockChainEventListener );
            }
        }
        
        return node;
    }
    
    @Override
    public Node syncLocalBlocks() 
    {
        if( node.getState() == NodeState.ACTIVE_NOTSYNC || node.getState() == NodeState.ACTIVE_SYNC ) 
        {
            peerGroup = (PeerGroup) node.getPeerGroup();
            
            if( peerGroup.isRunning()) 
            {
                node.getStatus().setOperationType( OperationType.SYNC_LOCAL_STORE );
                node.getStatus().setMessage( "Started Local Block synchronization !!!" );
                
                blockStoreService.syncData( node );
            }
        }
        
        return node;
    }

    
    @Override
    public GenericAddress getAddressDetails( String addressStr )
    {
        try
        {
            return null;//getOpenTxOutputs( addressStr );
        }
        catch( Exception e )
        {
            return null;
        }
    }
    
    
    @Override
    public List<GenericAddress> getAddressDetails( List<String> addressList )
    {
        try
        {
            List<GenericAddress> genAddressList = new ArrayList<>();
            
//            for(String addressStr: addressList ) 
//            {
//                //genAddressList.add( getOpenTxOutputs( addressStr ) );
//            }
//            
            return genAddressList;
        }
        catch( Exception e )
        {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<GenericBlock> getRecentBlocks( int recentCount )
    {
        List<GenericBlock> blocks = new ArrayList<>();
        
        try
        {
            StoredBlock storedBlock = null;//getFullBlockStore().getChainHead();

            for( int x = 1; x <= recentCount; x++ )
            {
                blocks.add( blockToGenericBlock( false, storedBlock ) );
                //storedBlock = storedBlock.getPrev( blockChain.getBlockStore() ); 
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return blocks;
    }

    @Override
    public GenericBlock getBlockByHash( String hash )
    {
        try
        {
            //Sha256Hash sha256Hash = Sha256Hash.wrap( hash );
            
            StoredBlock sBlock = null;//blockChain.getBlockStore().get( sha256Hash );
            
            return blockToGenericBlock( true, sBlock );
        }
        catch( Exception e )
        {
            return null;
        }
    }
    
    private void messageReceived( Message m ) 
    {
        Pong pong = m();
        MemoryPoolMessage mPool = (MemoryPoolMessage)m;
    }
    
    @Override
    public double getEstimatedTxFee()
    {
        class EventListener implements PreMessageReceivedEventListener 
        {

            @Override
            public Message onPreMessageReceived( Peer peer, Message m )
            {
                messageReceived( m );
                return m;
            }
            
        };
        
        double fee = 0;
        
        try
        {
            BlockChain blockChain = (BlockChain)node.getBlockChain();
            
            //getPeerMempoolTransaction( hash ) addPreMessageReceivedEventListener( new EventListener() ); 
            getConnectedPeer().sendMessage( new MemoryPoolMessage() );
            getConnectedPeer().sendMessage( new Ping( 100 ) );
            
        }
        catch( Exception e )
        {
        }
        
        return fee;
    }

    
    @Override
    public GenericTransaction getTransactionByHash( String blockHash, String txHash )
    {
//        try
//        {
//            StoredBlock storedBlock = null;//blockChain.getBlockStore().get( Sha256Hash.wrap( blockHash ) );
//            GenericBlock genBlock = blockToGenericBlock( false, storedBlock );
//            
//            Future<Block> futureBlock = getConnectedPeer().getBlock( storedBlock.getHeader().getHash() );
//            Block fullBlock = futureBlock.get();
//            
//            
//            if(fullBlock.hasTransactions()) 
//            {
//                for( Transaction tx : fullBlock.getTransactions() ) 
//                {
//                    if(tx.getHashAsString().equals( txHash ))
//                        return txToGenericTransaction( genBlock, tx ); 
//                }
//            }
//
//        }
//        catch( Exception e )
//        {
//        }
        
        return null;
    }


    private GenericBlock blockToGenericBlock( boolean fullData, StoredBlock storedBlock )
    {
        GenericBlock block = new GenericBlock( BITCOIN );
        
        try
        {
            Peer peer = getConnectedPeer();
            
            block.setMerkleRoot( storedBlock.getHeader().getMerkleRoot().toString() );
            block.setHeight( storedBlock.getHeight() );
            block.setNonce( storedBlock.getHeader().getNonce() );
            block.setTime( storedBlock.getHeader().getTime() );
            block.setVersion( storedBlock.getHeader().getVersion() );
            block.setPrevBlockHash( storedBlock.getHeader().getPrevBlockHash().toString());
            block.setHash( storedBlock.getHeader().getHashAsString() );
            
            if( fullData ) //Connect to the peer and get full info
            {
                Future<Block> futureBlock = peer.getBlock( storedBlock.getHeader().getHash() );
                Block fullBlock = futureBlock.get();
                block.setMessageSize( fullBlock.getMessageSize() );
                
                if(fullBlock.hasTransactions()) 
                {
                    for(Transaction tx : fullBlock.getTransactions()) 
                    {
                        txToGenericTransaction( block, tx );
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return block;
    }
    
    private GenericTransaction txToGenericTransaction( GenericBlock block, Transaction tx )
    {
        GenericTransaction genericTx = new GenericTransaction( block );
        genericTx.setHash( tx.getHashAsString() );
        genericTx.setVersion( tx.getVersion() );
        genericTx.setMessageSize(tx.getMessageSize());
        genericTx.setMemo( tx.getMemo() );
        genericTx.setStatus( tx.isPending() ? TransactionStatus.PENDING : TransactionStatus.ACTIVE );
        
        for( TransactionInput txInput: tx.getInputs()) 
        {
            txInputToGenericTxInput( genericTx, txInput );
        }
        
        for( TransactionOutput txInput: tx.getOutputs()) 
        {
            txOutputToGenericTxOutput( genericTx, txInput );
        }
        
        return genericTx;
    }
    
    private GenericTxInput txInputToGenericTxInput( GenericTransaction genericTx, TransactionInput txInput )
    {   
        GenericTxInput genericTxInput = new GenericTxInput( genericTx );
        
        try 
        {
            genericTxInput.getAmount().setValue(0 );
            genericTxInput.setCoinBase( txInput.isCoinBase() );
            genericTxInput.setCached( txInput.isCached() );
            
            if( !txInput.isCoinBase() ) 
            {
                if( txInput.getOutpoint() != null ) 
                {
                    genericTxInput.setHash( txInput.getOutpoint().getHash().toString());
                    //getConnectedPeer().getPeerMempoolTransaction( txInput.getOutpoint().getHash() );
                }
            }
        }
        catch( Exception e ) 
        {
            logger.error( "Error constructing txInput:{}", e.toString() );
        }
        
        return genericTxInput;
    }
    
    private GenericTxOutput txOutputToGenericTxOutput( GenericTransaction genericTx, TransactionOutput txOutput )
    {   
        GenericTxOutput genericTxOutput = new GenericTxOutput( genericTx );
        
        try 
        {
            double value = txOutput.getValue() != null?((double)txOutput.getValue().value/Coin.COIN.value):0 ;
            genericTxOutput.setIndex( txOutput.getIndex() );
            genericTxOutput.setHash( txOutput.getOutPointFor().getHash().toString());
            genericTxOutput.getAmount().setValue(value);
            genericTxOutput.setCached( txOutput.isCached() );
            
            Optional<Address> p2pKHAddr = Optional.ofNullable( txOutput.getAddressFromP2PKHScript( txOutput.getParams() ));
            Optional<Address> p2pHr = Optional.ofNullable(txOutput.getAddressFromP2SH( txOutput.getParams() ));
            
            if(p2pKHAddr.isPresent())
                genericTxOutput.setToAddress( p2pKHAddr.get().toString());
            else if(p2pHr.isPresent())
                genericTxOutput.setToAddress( p2pHr.get().toString());
            
            TransactionInput txInput = txOutput.getSpentBy();
            
            if(txInput != null) 
            {
                txInput.getHash();
            }
            
        }
        catch( Exception e )
        {
            logger.error( "Error constructing txOutput:{}", e.toString());
        }
        
        return genericTxOutput;
    }

   
}
