package com.eseict.kaist.repository;

import com.eseict.kaist.data.vo.fac.TblGasSensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GasSensorRepository extends JpaRepository<TblGasSensor, String> {
}
