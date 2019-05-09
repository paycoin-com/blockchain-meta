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
            "WHEN (abs(d8-d7)< d7 and abs(d8-d7)<d8) THEN v14 " + 
            "WHEN d8 < d7 THEN v28 " + 
            "ELSE v7 END AS rate from " + 
            "( select max(value2) d8 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '168:00:00.000000' " + 
            "and current_timestamp - date < '192:00:00.000000' " + 
            "order by value2 asc " + 
            "limit 20 ) DAY8) D8, " + 
            "( select max(value2) d7 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '144:00:00.000000' " + 
            "and current_timestamp - date < '168:00:00.000000' " + 
            "order by value2 asc " + 
            "limit 20 ) DAY7) D7, " + 
            "( select max(value1) v14 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '0:00:00.000000' " + 
            "and current_timestamp - date < '24:00:00.000000' " + 
            "order by value1 asc " + 
            "limit 14 ) DAY1) V1_14, " + 
            "( select max(value1) v7 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '0:00:00.000000' " + 
            "and current_timestamp - date < '24:00:00.000000' " + 
            "order by value1 asc " + 
            "limit 7 ) DAY1) V1_7, " + 
            "( select max(value1) v28 from ( " + 
            "select *, current_timestamp - date from tb_feeratedata " + 
            "where current_timestamp - date > '0:00:00.000000' " + 
            "and current_timestamp - date < '24:00:00.000000' " + 
            "order by value1 asc " + 
            "limit 28 " + 
            ") DAY1) V1_28", nativeQuery = true)
   Long calculateRate();
    
   @Query(value = "WITH " + 
           "borders as ( " + 
           "select min(E.estimaterate_id) as estimaterate_id, P.range, P.prev_value " + 
           "from tb_estimateratedetails E, tb_predictedvalues P " + 
           "where E.estimaterate_id = P.estimaterate_id " + 
           "and P.prev_value <> 0 " + 
           "and timestamp > now() - interval '30 minutes' " + 
           "and timestamp < now() " + 
           "group by P.range, P.prev_value), " + 
           "max_tmstp as ( " + 
           "select case when block_tmstp > '00:05:00' then block_tmstp " + 
           "else '00:05:00' end as block_tmstp " + 
           "from " + 
           "( " + 
           "select max(timestamp) + interval '20 minutes' - now()  as block_tmstp " + 
           "from tb_estimateratedetails E, borders B " + 
           "where E.estimaterate_id = B.estimaterate_id) " + 
           "A), " + 
           "one_per_estimate as ( " + 
           "select max(id) as id, estimaterate_id, range " + 
           "from tb_estimateratedetails " + 
           "where timestamp > now() - interval '30 minutes' and timestamp < now() " + 
           "group by estimaterate_id, range " + 
           "), " + 
           "offset_tb as ( " + 
           "Select E.estimaterate_id, E.range, sum(coalesce(F.prev_value,0)) " + 
           "from " + 
           "( " + 
           "select distinct estimaterate_id, range " + 
           "from tb_estimateratedetails " + 
           "where timestamp > now() - interval '30 minutes' and timestamp < now() " + 
           ") E " + 
           "LEFT OUTER JOIN " + 
           "borders F " + 
           "ON " + 
           "E.estimaterate_id >= F.estimaterate_id and E.range = F.range " + 
           "group by E.estimaterate_id, E.range), " + 
           "results as ( " + 
           "select AA.range, (AA.regr_intercept - coalesce(BB.sum, 0)) as finale " + 
           "from " + 
           "( " + 
           "Select distinct G.range, " + 
           "regr_intercept(G.sum+H.sum, extract(epoch from (now()-(G.timestamp-M.block_tmstp )))) " + 
           "OVER(Partition by G.range) " + 
           "FROM tb_estimateratedetails G, max_tmstp M, offset_tb H, one_per_estimate O " + 
           "where G.estimaterate_id = H.estimaterate_id and G.range = H.range and G.id = O.id " + 
           ") AA " + 
           "LEFT OUTER JOIN " + 
           "( select B.range, sum(B.prev_value) " + 
           "from borders B " + 
           "group by B.range ) BB " + 
           "ON AA.range = BB.range) " + 
           "Select min(range) from ( " + 
           "Select R1.range, sum(R2.finale) " + 
           "from results R1 " + 
           "LEFT OUTER JOIN results R2 " + 
           "ON R1.range <= R2.range " + 
           "group by R1.range " + 
           "order by R1.range desc " + 
           ") AAA " + 
           "where sum <= 1048576", nativeQuery = true)
   Integer estimateMediumRate();
   
   
    @Query(value = "WITH " + 
            "borders as ( " + 
            "select min(E.estimaterate_id) as estimaterate_id, P.range, P.prev_value " + 
            "from tb_estimateratedetails E, tb_predictedvalues P " + 
            "where E.estimaterate_id = P.estimaterate_id " + 
            "and P.prev_value <> 0 " + 
            "and timestamp > now() - interval '30 minutes' " + 
            "and timestamp < now() " + 
            "group by P.range, P.prev_value), " + 
            "max_tmstp as ( " + 
            "select case when block_tmstp > '00:05:00' then block_tmstp " + 
            "else '00:05:00' end as block_tmstp " + 
            "from " + 
            "( " + 
            "select max(timestamp) + interval '60 minutes' - now()  as block_tmstp " + 
            "from tb_estimateratedetails E, borders B " + 
            "where E.estimaterate_id = B.estimaterate_id) " + 
            "A), " + 
            "one_per_estimate as ( " + 
            "select max(id) as id, estimaterate_id, range " + 
            "from tb_estimateratedetails " + 
            "where timestamp > now() - interval '30 minutes' and timestamp < now() " + 
            "group by estimaterate_id, range " + 
            "), " + 
            "offset_tb as ( " + 
            "Select E.estimaterate_id, E.range, sum(coalesce(F.prev_value,0)) " + 
            "from " + 
            "( " + 
            "select distinct estimaterate_id, range " + 
            "from tb_estimateratedetails " + 
            "where timestamp > now() - interval '30 minutes' and timestamp < now() " + 
            ") E " + 
            "LEFT OUTER JOIN " + 
            "borders F " + 
            "ON " + 
            "E.estimaterate_id >= F.estimaterate_id and E.range = F.range " + 
            "group by E.estimaterate_id, E.range), " + 
            "results as ( " + 
            "select AA.range, (AA.regr_intercept - coalesce(BB.sum, 0)) as finale " + 
            "from " + 
            "( " + 
            "Select distinct G.range, " + 
            "regr_intercept(G.sum+H.sum, extract(epoch from (now()-(G.timestamp-M.block_tmstp )))) " + 
            "OVER(Partition by G.range) " + 
            "FROM tb_estimateratedetails G, max_tmstp M, offset_tb H, one_per_estimate O " + 
            "where G.estimaterate_id = H.estimaterate_id and G.range = H.range and G.id = O.id " + 
            ") AA " + 
            "LEFT OUTER JOIN " + 
            "( " + 
            "select B.range, sum(B.prev_value) " + 
            "from borders B " + 
            "group by B.range ) BB " + 
            "ON AA.range = BB.range) " + 
            "Select min(range) from ( " + 
            "Select R1.range, sum(R2.finale) " + 
            "from results R1 " + 
            "LEFT OUTER JOIN results R2 " + 
            "ON R1.range <= R2.range " + 
            "group by R1.range " + 
            "order by R1.range desc " + 
            ") AAA " + 
            "where sum <= 1048576", nativeQuery = true)
    Integer estimateHighRate();

}
