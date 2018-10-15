package io.hs.bex.blocknode.handler.bitcoin;


import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.PostgresFullPrunedBlockStore;


public class BtcFullBlockStore extends PostgresFullPrunedBlockStore
{

    //private static final String VERIFIED_CHAIN_HEAD_SETTING = "verifiedchainhead";

    private static final String SELECT_HEADERS_SQL_BY_HEIGHT = "SELECT chainwork, height, header, wasundoable FROM headers WHERE height = ?";
    
    public BtcFullBlockStore( NetworkParameters params, int fullStoreDepth, String hostname, String dbName,
            String username, String password ) throws BlockStoreException
    {
        super( params, fullStoreDepth, hostname, dbName, username, password );
    }
    
    public StoredBlock getByHeight( long height ) throws BlockStoreException
    {
        return getByHeight( height, false );
    }
    
    private StoredBlock getByHeight( long sHeight, boolean wasUndoableOnly ) throws BlockStoreException
    {
        maybeConnect();
        PreparedStatement s = null;
        try
        {
            s = conn.get().prepareStatement( SELECT_HEADERS_SQL_BY_HEIGHT );
            s.setLong( 1, sHeight ); 
            ResultSet results = s.executeQuery();
            if( !results.next() )
            {
                return null;
            }
            // Parse it.

            if( wasUndoableOnly && !results.getBoolean( 4 ) )
                return null;

            BigInteger chainWork = new BigInteger( results.getBytes( 1 ) );
            int height = results.getInt( 2 );
            Block b = params.getDefaultSerializer().makeBlock( results.getBytes( 3 ) );
            b.verifyHeader();
            StoredBlock stored = new StoredBlock( b, chainWork, height );
            return stored;
        }
        catch( SQLException ex )
        {
            throw new BlockStoreException( ex );
        }
        catch( ProtocolException e )
        {
            // Corrupted database.
            throw new BlockStoreException( e );
        }
        catch( VerificationException e )
        {
            // Should not be able to happen unless the database contains bad
            // blocks.
            throw new BlockStoreException( e );
        }
        finally
        {
            if( s != null )
            {
                try
                {
                    s.close();
                }
                catch( SQLException e )
                {
                    throw new BlockStoreException( "Failed to close PreparedStatement" );
                }
            }
        }
    }
    
    /*
    @Override
    public void setVerifiedChainHead(StoredBlock chainHead) throws BlockStoreException 
    {
        Sha256Hash hash = chainHead.getHeader().getHash();
        this.verifiedChainHeadHash = hash;
        this.verifiedChainHeadBlock = chainHead;
        maybeConnect();
        try {
            PreparedStatement s = conn.get()
                    .prepareStatement(getUpdateSettingsSLQ());
            s.setString(2, VERIFIED_CHAIN_HEAD_SETTING);
            s.setBytes(1, hash.getBytes());
            s.executeUpdate();
            s.close();
        } catch (SQLException ex) {
            throw new BlockStoreException(ex);
        }
        if (this.chainHeadBlock.getHeight() < chainHead.getHeight())
            setChainHead(chainHead);
        removeUndoableBlocksWhereHeightIsLessThan(chainHead.getHeight() - fullStoreDepth);
    }*/

}
