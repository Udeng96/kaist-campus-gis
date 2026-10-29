package com.eseict.flame.repository;


import com.eseict.flame.data.vo.event.TblEventHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventRepository extends JpaRepository<TblEventHist, String> {
    List<TblEventHist> findByMappBuildingIdAndTypeAndClrDtm(String mappBuildingId, String type, String clrDtm);

    // 미해제 이벤트 전체 (구동 초기화용)
    List<TblEventHist> findByTypeAndClrDtm(String type, String clrDtm);

    // 자정 일괄 해제: 미해제(clr_dtm='') 불꽃 이벤트를 모두 해제 처리
    @Modifying
    @Transactional
    @Query("UPDATE TblEventHist e SET e.clrDtm = :clrDtm WHERE e.type = 'flame' AND e.clrDtm = ''")
    int clearAllUnresolvedFlame(@Param("clrDtm") String clrDtm);
}
