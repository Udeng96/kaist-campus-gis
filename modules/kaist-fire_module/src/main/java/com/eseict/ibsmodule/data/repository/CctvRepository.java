package com.eseict.ibsmodule.data.repository;

import com.eseict.ibsmodule.data.vo.Cctv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CctvRepository extends JpaRepository<Cctv, Long> {

    @Query("SELECT c FROM Cctv c WHERE c.buildingId = :buildingId AND c.streamId NOT LIKE '%HIGH'")
    List<Cctv> findByBuildingId(@Param("buildingId") Long buildingId);
}
