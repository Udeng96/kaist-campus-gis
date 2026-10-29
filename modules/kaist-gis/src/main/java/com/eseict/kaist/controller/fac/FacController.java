package com.eseict.kaist.controller.fac;


import com.eseict.kaist.data.dto.FavoriteBodyType;
import com.eseict.kaist.data.dto.SensorResponse;
import com.eseict.kaist.service.fac.FacService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.eseict.kaist.config.ApiConstant.API_PRODUCES;

@RestController
@RequestMapping(value = "/fac", produces = API_PRODUCES)
@Slf4j
@RequiredArgsConstructor
public class FacController {

    private final FacService facService;

    @GetMapping("/favorite")
    public ResponseEntity<FavoriteBodyType> getFavorites() {
        return ResponseEntity.ok(facService.getFavorites());
    }

    @PutMapping("/favorite")
    public ResponseEntity<Boolean> updateFavorites(
            @RequestBody FavoriteBodyType ids) {
        log.info("ids:{}", ids);
        return ResponseEntity.ok(facService.updateFavorites(ids));
    }

    @GetMapping("/sensors")
    public ResponseEntity<SensorResponse> getSensors(){
        return ResponseEntity.ok(facService.getSensors());
    }


}
