package io.hs.bex.blockchain.handler.bitcoin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

import io.hs.bex.blockchain.model.LocalStoreStatus;


@Component
public class BlockChainDAO
{
    // ---------------------------------
    private static final Logger logger = LoggerFactory.getLogger( BlockChainDAO.class );
    // ---------------------------------
    
    @Autowired
    HikariDataSource  dataSource;
    
    public byte[] getAddressData( String address, String network ) throws SQLException 
    {
        String SQL_QUERY = "select * from address_data where hash=? and network=? ";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setString( 1, address );
            pst.setString( 2, network );
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next())
                return rs.getBytes( "payload" );
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get address data:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
        
        return null;
    }
    
    public LocalStoreStatus getLocalStoreStatus() throws SQLException 
    {
        String SQL_QUERY = "select * from status_data where id=?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setInt( 1, LocalStoreStatus.STATUS_DATA_ID );
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) 
                return new LocalStoreStatus(rs.getLong( "block" ), rs.getLong( "tx" ), rs.getLong( "address" ),
                        rs.getString( "block_hash" ));
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get local store status:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
        
        return null;
    }
    
    public LocalStoreStatus saveLocalStoreStatus( LocalStoreStatus storeStatus ) throws SQLException 
    {
        String SQL_QUERY = "insert into status_data (id,block,tx,address,block_hash) values( ?, ?, ?, ?, ? )";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            con.setAutoCommit( true );
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setInt( 1, LocalStoreStatus.STATUS_DATA_ID );
            pst.setLong( 2, storeStatus.getBlockIndex() );
            pst.setLong( 3, storeStatus.getTxIndex() );
            pst.setLong( 4, storeStatus.getAddressIndex() );
            pst.setString( 5, storeStatus.getBlockHash());
            
            pst.execute();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in save local store status:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
        
        return storeStatus;
    }
    
    
    public void updateLocalStoreStatus( LocalStoreStatus storeStatus ) throws SQLException 
    {
        String SQL_QUERY = "update status_data set block=?, tx=?, address=?, block_hash=? where id=?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            con.setAutoCommit( true );
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, storeStatus.getBlockIndex() );
            pst.setLong( 2, storeStatus.getTxIndex() );
            pst.setLong( 3, storeStatus.getAddressIndex() );
            pst.setString( 4, storeStatus.getBlockHash());
            pst.setInt( 5, LocalStoreStatus.STATUS_DATA_ID );

            pst.executeUpdate();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in update local store status:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
    }
    
    public void saveAddressData( String address,String network, byte[] payload ) throws SQLException 
    {
        String SQL_QUERY = "insert into address_data (hash,network,payload) values( ?, ?, ? )";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            con.setAutoCommit( true );
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setString( 1, address );
            pst.setString( 2, network );
            pst.setBytes( 3, payload ); 

            pst.execute();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in save address data:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
    }
    
    public void updateAddressData( String address, String network, byte[] payload ) throws SQLException 
    {
        String SQL_QUERY = "update address_data set payload=? where hash=? and network=?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            con.setAutoCommit( true );
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setString( 2, address );
            pst.setString( 3, network );
            pst.setBytes( 1, payload ); 

            pst.executeUpdate();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in update address data:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
    }
    
    
    public void saveTxData( String hash, String network, byte[] payload ) throws SQLException 
    {
        String SQL_QUERY = "insert into tx_data (hash,network,payload) values(?,?,?)";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            con.setAutoCommit( true );
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setString( 1, hash );
            pst.setString( 2, network );
            pst.setBytes( 3, payload ); 

            pst.execute();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in save Tx data:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
    }


    public void saveBlockData( String hash, String network, byte[] payload ) throws SQLException 
    {
        String SQL_QUERY = "insert into block_data (hash,network,payload) values(?,?,?)";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            con.setAutoCommit( true );
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setString( 1, hash );
            pst.setString( 2, network );
            pst.setBytes( 3, payload ); 

            pst.execute();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in save block data:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
    }
    
    public long getBlockDataCount( long index ) throws SQLException 
    {
        String SQL_QUERY = "select count(*) from block_data where index > ?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, index );
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) 
            { 
                return rs.getLong( 1 ); 
            }
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get block count:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
        
        return 0;
    }
    
    
    public ResultSet getBlockData( long index ) throws SQLException 
    {
        String SQL_QUERY = "select * from block_data where index > ?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, index );
            
            return pst.executeQuery();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get block count:" , e );
        }
        
        return null;
    }
    
    public ResultSet getTxData( long index ) throws SQLException 
    {
        String SQL_QUERY = "select * from tx_data where index > ?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, index );
            
            return pst.executeQuery();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get block count:" , e );
        }
        
        return null;
    }

    public ResultSet getAddressData( long index ) throws SQLException 
    {
        String SQL_QUERY = "select * from address_data where index > ?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, index );
            
            return pst.executeQuery();
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get block count:" , e );
        }
        
        return null;
    }

    public long getTxDataCount( long index ) throws SQLException 
    {
        String SQL_QUERY = "select count(*) from tx_data where index > ?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, index );
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) 
            { 
                return rs.getLong( 1 ); 
            }
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get tx count:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
        
        return 0;
    }

    public long getAddressDataCount( long index ) throws SQLException 
    {
        String SQL_QUERY = "select count(*) from address_data where index > ?";
        Connection con = null;
        
        try
        {
            con = dataSource.getConnection();
            PreparedStatement pst = con.prepareStatement( SQL_QUERY );
            pst.setLong( 1, index );
            
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()) 
            { 
                return rs.getLong( 1 ); 
            }
        } 
        catch( Exception  e) 
        {
            logger.error( "(!) Error in get addressa count:" , e );
        }
        finally 
        {
            if( con != null )
                con.close();
        }
        
        return 0;
    }


}
