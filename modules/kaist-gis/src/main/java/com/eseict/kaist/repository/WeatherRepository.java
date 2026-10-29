package com.eseict.kaist.repository;

import com.eseict.kaist.data.vo.publi.TblWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WeatherRepository extends JpaRepository<TblWeather, String> {

    List<TblWeather> findByRegDtm(String regDtm);

    @Modifying
    @Transactional
    @Query("UPDATE TblWeather e SET e.updDtm = :updDtm WHERE e.regDtm = :regDtm")
    void updateUpdDtm(@Param("updDtm") String updDtm, @Param("regDtm") String regDtm);

    @Modifying
    @Transactional
    @Query("UPDATE TblWeather e SET e.reportContent = :reportContent WHERE e.regDtm = :regDtm")
    int updateReportContent(@Param("reportContent") String reportContent, @Param("regDtm") String regDtm);

    TblWeather findFirstByOrderByRegDtmDesc();
}
