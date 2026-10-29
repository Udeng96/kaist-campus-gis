package com.eseict.kaist.repository;


import com.eseict.kaist.data.vo.fac.TblFireSensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FireSensorRepository extends JpaRepository<TblFireSensor, String> {

}
