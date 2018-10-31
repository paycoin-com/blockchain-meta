package io.hs.bex.blockchain.dao.api;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.hs.bex.blockchain.model.store.BlockChainData;

public interface BlockChainDAO extends JpaRepository<BlockChainData, Long>
{
    //********************************************
    @Query(value = "SELECT b "
            + " FROM BlockChainData b"
            + " WHERE b.status=:status"
            + " AND b.nodeId=:nodeId"
            + " ORDER BY b.height DESC ")
    Page<BlockChainData> findByStatus( @Param( "status" ) short status,@Param( "nodeId" ) short nodeId, Pageable p );
    
    //********************************************
    @Modifying
    @Query(value = "update BlockChainData b "
            + " set b.status=:status"
            + " WHERE b.id in :ids" )
    void updateStatus( @Param( "ids" ) Long[] ids, @Param( "status" ) short status );

}