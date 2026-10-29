package com.eseict.kaist.controller.fac;


import com.eseict.kaist.data.vo.fac.TblBuilding;
import com.eseict.kaist.service.fac.BuildingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.eseict.kaist.config.ApiConstant.API_PRODUCES;

@RestController
@RequestMapping(value = "/building", produces = API_PRODUCES)
@Slf4j
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("")
    public ResponseEntity<List<TblBuilding>> getBuildings() {
        return ResponseEntity.ok(buildingService.getBuildings());
    }

}
