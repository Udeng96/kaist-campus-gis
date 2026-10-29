package com.eseict.ibsmodule.data.repository;

import com.eseict.ibsmodule.data.vo.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.type = :type AND e.buildingId = :buildingId AND e.clearedAt IS NULL")
    List<Event> findActiveByTypeAndBuildingId(@Param("type") String type, @Param("buildingId") Long buildingId);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.clearedAt = :clearedAt WHERE e.id = :id")
    int updateClearedAt(@Param("clearedAt") OffsetDateTime clearedAt, @Param("id") Long id);
}
