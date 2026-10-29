package com.eseict.kaist.repository;


import com.eseict.kaist.data.vo.fac.TblBuilding;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<TblBuilding, String> {

    List<TblBuilding> findAll(Sort sort);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE fac.tbl_building " +
                    "SET favorite_yn = CASE " +
                    "    WHEN id IN (:ids) THEN '1' " +
                    "    ELSE '0' " +
                    "END",
            nativeQuery = true
    )
    int updateFavorites(@Param("ids") List<String> ids);

    List<TblBuilding> findByFavoriteYnIs(String favoriteYn);
}
