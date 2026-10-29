package com.eseict.kaist.repository;


import com.eseict.kaist.data.vo.event.TblEventPatrol;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PatrolRepository extends JpaRepository<TblEventPatrol, String> {

    List<TblEventPatrol> findAll(Sort sort);

    @Modifying
    @Transactional
    @Query("delete from TblEventPatrol p where p.id = :id")
    int deleteByIdAsInt(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("update TblEventPatrol p set p.name = :name, p.cctvMappId = :cctvs where p.id = :id")
    int updateByIdAsInt(@Param("id") String id, @Param("name") String name, @Param("cctvs") String cctvs);
}
