package com.eseict.flame.repository;

import com.eseict.flame.data.vo.event.TblFlameLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<TblFlameLog, String> {
    boolean existsBySensorIdAndInsertDtmStartingWith(String sensorId, String insertDtmPrefix);
}
