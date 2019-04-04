package io.hs.bex.blockchain.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hs.bex.blockchain.model.store.FeeRateData;

public interface FeeRateDataDAO extends JpaRepository<FeeRateData, Long>
{
    //********************************************
    @Query(value = "SELECT f "
            + " FROM FeeRateData f "
            + " order by  f.date DESC")
    Page<FeeRateData> getLatest( Pageable page );

    //********************************************
    @Query(value = "SELECT " + 
            "CASE " + 
            "WHEN abs(d8-d7)<0.25*d8 THEN v14 " + 
            "WHEN d8 < d7 THEN v28 " + 
            "ELSE v7 " + 
            "END " + 
            "AS rate " + 
            "from " + 
            "( " + 
            "select max(value2) d8 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '168:00:00.000000' " + 
            "and current_timestamp - date < '192:00:00.000000' " + 
            "order by value2 asc " + 
            "limit 20 " + 
            ") DAY8) D8, " + 
            "( " + 
            "select max(value2) d7 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '144:00:00.000000' " + 
            "and current_timestamp - date < '168:00:00.000000' " + 
            "order by value2 asc " + 
            "limit 20 " + 
            ") DAY7) D7, " + 
            "( " + 
            "select max(value1) v14 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '0:00:00.000000' " + 
            "and current_timestamp - date < '24:00:00.000000' " + 
            "order by value1 asc " + 
            "limit 14 " + 
            ") DAY1) V1_14, " + 
            "( " + 
            "select max(value1) v7 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '0:00:00.000000' " + 
            "and current_timestamp - date < '24:00:00.000000' " + 
            "order by value1 asc " + 
            "limit 7 " + 
            ") DAY1) V1_7, " + 
            "( " + 
            "select max(value1) v28 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '0:00:00.000000' " + 
            "and current_timestamp - date < '24:00:00.000000' " + 
            "order by value1 asc " + 
            "limit 28 " + 
            ") DAY1) V1_28", nativeQuery = true)
   Long calculateRate();

}
