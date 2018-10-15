package io.hs.bex.blocknode.handler.bitcoin.listeners;

import org.bitcoinj.core.Peer;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerEventListener implements PeerConnectedEventListener
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( PeerEventListener.class );
    // ---------------------------------

    @Override
    public void onPeerConnected( Peer peer, int peerCount )
    {
        logger.info( "(!!!) Peer connected:{}" , peer );
    }

}
