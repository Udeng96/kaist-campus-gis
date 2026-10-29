package com.eseict.ibsmodule.data.repository;

import com.eseict.ibsmodule.data.vo.FireSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<FireSensor, Long> {

    Optional<FireSensor> findBySensorId(String sensorId);

    List<FireSensor> findByPanel(String panel);
}
