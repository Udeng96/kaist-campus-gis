package com.eseict.kaist.service.fac;


import com.eseict.kaist.data.vo.fac.TblCctv;
import com.eseict.kaist.repository.CctvRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class Cctvservice {

    private final CctvRepository cctvRepository;

    public List<TblCctv> getCctvs() {
        Sort sort = Sort.by(Sort.Direction.ASC, "cctvNm");

        return cctvRepository.findAll(sort);
    }

    public List<TblCctv> createCctv(TblCctv cctv) {
        Sort sort = Sort.by(Sort.Direction.ASC, "cctvNm");

        TblCctv result = cctvRepository.save(cctv);
        log.info("{} CCTV가 정상적으로 저장되었습니다.", result.getCctvNm());
        return cctvRepository.findAll(sort);

    }

    @Transactional
    public List<TblCctv> updateCctv(TblCctv newCctv) {
        // 업데이트 실행
        Sort sort = Sort.by(Sort.Direction.ASC, "cctvNm");

        int deleteResult = cctvRepository.deleteByStreamId((newCctv.getStreamId()));
        if(deleteResult == 1){
            TblCctv result = cctvRepository.save(newCctv);
            log.info("{} CCTV가 정상적으로 수정되었습니다.", result.getCctvNm());
        }

        return cctvRepository.findAll(sort);
    }

    public int deleteCctv(String streamId) {
        return cctvRepository.deleteByStreamId(streamId);
    }
}
