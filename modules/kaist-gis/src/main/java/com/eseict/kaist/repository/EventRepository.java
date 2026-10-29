package com.eseict.kaist.repository;

import com.eseict.kaist.data.vo.event.TblEventHist;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<TblEventHist, String> {
    // 전체 + 정렬
    List<TblEventHist> findAll(Sort sort);

    // 건물코드 Like + 정렬
    List<TblEventHist> findByMappBuildingIdLike(String mappBuildingId, Sort sort);

    // 타입 + 정렬
    List<TblEventHist> findByType(String type, Sort sort);

    // 건물코드 Like + 타입 + 정렬 (두 조건 모두 있을 때)
    List<TblEventHist> findByMappBuildingIdLikeAndType(String mappBuildingId, String type, Sort sort);

    List<TblEventHist> findByOutbDtmBetween(String outbDtmAfter, String outbDtmBefore, Sort sort);

    List<TblEventHist> findByMappBuildingIdLikeAndOutbDtmBetween(String mappBuildingId, String outbDtmAfter, String outbDtmBefore, Sort sort);

    List<TblEventHist> findByMappBuildingIdLikeAndTypeAndOutbDtmBetween(String mappBuildingId, String type, String outbDtmAfter, String outbDtmBefore, Sort sort);


    @Modifying
    @Transactional
    @Query("UPDATE TblEventHist e SET e.clrDtm = :clrDtm WHERE e.seqn = :seqn")
    int updateClrDtm(@Param("clrDtm") String clrDtm, @Param("seqn") String seqn);

    List<TblEventHist> findByMappBuildingIdAndOutbDtmBetween(String buildId, String startDtm, String endDtm, Sort sort);
}