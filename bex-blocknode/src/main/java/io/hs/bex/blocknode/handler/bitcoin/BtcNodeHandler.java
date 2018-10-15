package io.hs.bex.blocknode.handler.bitcoin;


import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Nullable;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import io.hs.bex.blocknode.handler.bitcoin.listeners.*;
import io.hs.bex.blocknode.handler.bitcoin.model.MainNetParams;
import io.hs.bex.blocknode.handler.bitcoin.model.NetworkParams;
import io.hs.bex.blocknode.handler.bitcoin.model.RegTestParams;
import io.hs.bex.blocknode.handler.bitcoin.model.TestNet3Params;
import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeNetworkType;
import io.hs.bex.blocknode.model.NodeProvider;
import io.hs.bex.blocknode.model.NodeState;
import io.hs.bex.blocknode.model.NodeStatus;
import io.hs.bex.blocknode.model.OperationType;
import io.hs.bex.blocknode.service.api.GenericNodeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;


@Service( "BitcoinNodeHandler" )
@Scope("prototype")
public class BtcNodeHandler implements GenericNodeHandler
{

    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BtcNodeHandler.class );
    // ---------------------------------
    
    private Node node = new Node();
    
    @Autowired
    private Environment env;
    
    private String peerHosts;
    
    private String dbHost;

    private String dbName;

    private String dbUsername;

    private String dbPassword;

    private NetworkParams params;
    
    BlockStore blockStore;
    
    //FullPrunedBlockChain 
    BlockChain blockChain;
    
    PeerGroup peerGroup;
    
    Peer peer;
    
    protected PeerEventListener peerEventListener;

    private Context bitcoinContext = null;
    
    @Override
    public Node init( NodeProvider nodeProvider )
    {
        String prefix = ""; 
                
        if( nodeProvider.getNetworkType() == NodeNetworkType.TESTNET)
            prefix = "-testnet3";
        else if( nodeProvider.getNetworkType() == NodeNetworkType.REGTEST)
            prefix = "-regtest";
                
        peerHosts = env.getProperty( "node.bitcoin" + prefix + ".peer.host" );
        dbHost = env.getProperty( "node.bitcoin" + prefix + ".db.host");
        dbName = env.getProperty( "node.bitcoin" + prefix + ".db.name");
        dbUsername = env.getProperty( "node.bitcoin" + prefix + ".db.username");
        dbPassword = env.getProperty( "node.bitcoin" + prefix + ".db.password");
        
        return init( nodeProvider, false );
    }
    
    
    public Node init( NodeProvider nodeProvider, boolean fullVerificationMode )
    {
        node.setId( nodeProvider.getId());
        node.setFullVerificationMode( fullVerificationMode );
        node.setName( "Bitcoin-" + nodeProvider.getNetworkType().name().toLowerCase() );
        node.getNetwork().setType( nodeProvider.getNetworkType() );
        node.getNetwork().setHost( dbHost );
        
        if(nodeProvider.getNetworkType() == NodeNetworkType.TESTNET )
            params = TestNet3Params.get();
        else if(nodeProvider.getNetworkType() == NodeNetworkType.REGTEST )
        {
            params = RegTestParams.get();
        }
        else
            params = MainNetParams.get();
        
        //-----------------------------------------
        try
        {
            blockStore = getBlockStore( params, 10000 );
            blockChain = getBlockChain( params, blockStore ) ;
            node.setBlockChain( blockChain );
        }
        catch( BlockStoreException e )
        {
            logger.error( "(!) Error initializing node:" + e.toString() );
        }
        //-----------------------------------------
        
        return node;
    }

    public Node start() throws BlockStoreException, UnknownHostException
    {
        peerGroup = getPeerGroup( params, blockChain );
        node.setPeerGroup( peerGroup );
        
        peerEventListener = new PeerEventListener();
        peerGroup.addConnectedEventListener( peerEventListener );
        
        if(!Strings.isNullOrEmpty( peerHosts )) 
        {
            String phosts[] = peerHosts.split( "," );
            
            for( String peerHost:phosts )
            {
                peerGroup.addAddress( InetAddress.getByName( peerHost.trim() ) );
                
                //--------------------------------------------
                logger.info( "Added peer Host:{}",peerHost );
                //--------------------------------------------
            }
        }
        
        startPeerGroup( false );
        
        setNodeStatus( node, NodeState.ACTIVE_NOTSYNC ,OperationType.INIT, 
                "Successfully started node."); 

        return node;
    }
    
    public Node stop()
    {
        stopPeerGroup();
        
        setNodeStatus( node, NodeState.INACTIVE ,OperationType.IDLE, "Successfully stopped Peer. "); 
        
        return node;
    }    
    
    private BtcFullBlockStore getBlockStore( NetworkParameters params ,int fullStoreDepth ) 
            throws BlockStoreException 
    {
        
        return new BtcFullBlockStore(params, fullStoreDepth, dbHost, dbName, dbUsername, dbPassword);
    }
    
    private BlockChain getBlockChain( NetworkParameters params, BlockStore blockStore ) 
            throws BlockStoreException
    {
        bitcoinContext = new Context(params);
        return new BlockChain( bitcoinContext, blockStore );
    }
    
    private PeerGroup getPeerGroup( NetworkParameters params, BlockChain chain ) throws BlockStoreException
    {
        return new PeerGroup( bitcoinContext, chain );
    }
  
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private void startPeerGroup( boolean async ) 
    {
        if( async ) 
        {
            Futures.addCallback( peerGroup.startAsync(), new FutureCallback() 
            {
                @Override
                public void onSuccess( @Nullable Object result ) 
                {
                    setNodeStatus( node, NodeState.ACTIVE_NOTSYNC ,OperationType.INIT, 
                            "Successfully Started node."); 
                }
    
                @Override
                public void onFailure(Throwable t) 
                {
                    setNodeStatus( node, NodeState.ACTIVE_NOTSYNC ,OperationType.HALT, 
                            "Error downloading blocks!" + t.getCause() ); 
    
                    throw new RuntimeException(t);
                }
            });
        }
        else 
        {
            peerGroup.start();
        }
    }
    
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private void stopPeerGroup() 
    {
        Futures.addCallback( peerGroup.stopAsync(), new FutureCallback() 
        {
            @Override
            public void onSuccess( @Nullable Object result ) 
            {
                setNodeStatus( node, NodeState.INACTIVE ,OperationType.IDLE, "Successfully stopped Peer. "); 
            }

            @Override
            public void onFailure(Throwable t) 
            {
                throw new RuntimeException(t);
            }
        });
    }
    
    
    private void setNodeStatus( Node node, NodeState state ,OperationType operationType, String message ) 
    {
        if( node != null ) 
        {
            node.setState( state );
            node.getStatus().setOperationType( operationType );
            node.getStatus().setMessage( message );
        }
    }
    
    public NodeStatus getStatus() 
    {
        return node.getStatus();
    }

    @Override
    public Node getNode()
    {
        return node;
    }

}
