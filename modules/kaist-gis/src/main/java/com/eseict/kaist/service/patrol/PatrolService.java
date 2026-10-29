package com.eseict.kaist.service.patrol;

import com.eseict.kaist.data.vo.event.TblEventPatrol;
import com.eseict.kaist.repository.PatrolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatrolService {

    private final PatrolRepository patrolRepository;

    public List<TblEventPatrol> getPatrols() {
        return patrolRepository.findAll(Sort.by(Sort.Direction.DESC, "regDtm"));
    }

    public List<TblEventPatrol> savePatrols(TblEventPatrol patrol) {
        if(patrol.getId().isEmpty()){
            patrol.setId(generatePatrolId());
        }
        TblEventPatrol result = patrolRepository.save(patrol);
        if(result.getId().equals(patrol.getId())) {
            log.info("SUCCESS TO SAVE PATROL");
        }else{
            log.info("FAIL TO SAVE PATORL");
        }

        return getPatrols();
    }

    public List<TblEventPatrol> delPatrols(String patrolId) {
        int delResult = patrolRepository.deleteByIdAsInt(patrolId);
        if (delResult > 0) {
            log.info("SUCCESS TO DELETE PATROL");
        }else{
            log.info("FAILURE TO DELETE PATROL");
        }
        return getPatrols();
    }

    public List<TblEventPatrol> updatePatrols(String id, String name, String cctvs) {
        int updateResult = patrolRepository.updateByIdAsInt(id, name, cctvs);
        if(updateResult > 0){
            log.info("SUCCESS TO UPDATE PATROL");
        }else{
            log.info("FAILURE TO UPDATE PATROL");
        }

        return getPatrols();
    }

    private String generatePatrolId() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }


}
