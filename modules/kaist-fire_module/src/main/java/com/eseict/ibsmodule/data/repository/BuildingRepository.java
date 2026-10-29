package com.eseict.ibsmodule.data.repository;

import com.eseict.ibsmodule.data.vo.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    Optional<Building> findByCode(String code);
}
