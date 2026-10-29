package com.eseict.kaist.controller.patrol;


import com.eseict.kaist.data.vo.event.TblEventPatrol;
import com.eseict.kaist.service.patrol.PatrolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.eseict.kaist.config.ApiConstant.API_PRODUCES;

@RestController
@RequestMapping(value = "/patrol", produces = API_PRODUCES)
@Slf4j
@RequiredArgsConstructor
public class PatrolController {

    private final PatrolService patrolService;

    @GetMapping("")
    public ResponseEntity<List<TblEventPatrol>> getPatrols(
    ){
        return ResponseEntity.ok(patrolService.getPatrols());
    }

    @PostMapping("")
    public ResponseEntity<List<TblEventPatrol>> createPatrol(
            @RequestBody TblEventPatrol patrol
    ){
        return ResponseEntity.ok(patrolService.savePatrols(patrol));
    }

    @PutMapping("")
    public ResponseEntity<List<TblEventPatrol>> updateFavorites(@RequestBody TblEventPatrol patrol) {
        return ResponseEntity.ok(patrolService.updatePatrols(patrol.getId(), patrol.getName(), patrol.getCctvMappId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<TblEventPatrol>> deletePatrols(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(patrolService.delPatrols(id));
    }
}
