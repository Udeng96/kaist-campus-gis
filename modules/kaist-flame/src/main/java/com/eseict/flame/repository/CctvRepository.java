package com.eseict.flame.repository;


import com.eseict.flame.data.vo.fac.TblCctv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CctvRepository extends JpaRepository<TblCctv, String> {

    TblCctv findByPlcTypeAndStreamId(String plcType, String streamId);

    List<TblCctv> findByBuildingLikeAndStreamIdNotLike(String building, String streamId);
}
