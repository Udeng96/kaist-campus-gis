package com.eseict.kaist.repository;


import com.eseict.kaist.data.vo.fac.TblCctv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface CctvRepository extends JpaRepository<TblCctv, String> {


    @Modifying
    @Transactional
    int deleteByStreamId(String streamId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE fac.tbl_cctv " +
                    "SET favorite_yn = CASE " +
                    "    WHEN stream_id IN (:ids) THEN '1' " +
                    "    ELSE '0' " +
                    "END",
            nativeQuery = true
    )
    int updateFavorites(@Param("ids") List<String> ids);

    List<TblCctv> findByFavoriteYnIs(String favoriteYn);

    List<TblCctv> findByPlcType(String plcType);
}
