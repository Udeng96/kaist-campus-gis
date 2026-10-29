package com.eseict.ibsmodule.data.repository;

import com.eseict.ibsmodule.data.vo.FireLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRepository extends JpaRepository<FireLog, Long> {

    @Query("SELECT fl FROM FireLog fl WHERE fl.detectedAt = " +
           "(SELECT MAX(fl2.detectedAt) FROM FireLog fl2 WHERE fl2.sensorId = fl.sensorId)")
    List<FireLog> findLatestLogPerSensor();
}
