package io.hs.bex.blockchain.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hs.bex.blockchain.model.store.EstimateFeeRate;

public interface EstimateFeeRateDAO extends JpaRepository<EstimateFeeRate, Long>
{
    //********************************************
    @Query(value = "SELECT e "
            + " FROM EstimateFeeRate e "
            + " order by e.timestamp DESC")
    Page<EstimateFeeRate> getLatest( Pageable page );

}
