package com.eseict.kaist.controller.fac;

import com.eseict.kaist.data.vo.fac.TblCctv;
import com.eseict.kaist.service.fac.Cctvservice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.eseict.kaist.config.ApiConstant.API_PRODUCES;

@RestController
@RequestMapping(value = "/cctv", produces = API_PRODUCES)
@Slf4j
@RequiredArgsConstructor
public class CctvController {

    private final Cctvservice cctvservice;

    @GetMapping("")
    public ResponseEntity<List<TblCctv>> getCctvs() {
        return ResponseEntity.ok(cctvservice.getCctvs());
    }

    @PostMapping("")
    public ResponseEntity<List<TblCctv>> createCctv(
            @RequestBody TblCctv cctv
    ) {
        return ResponseEntity.ok(cctvservice.createCctv(cctv));
    }

    @DeleteMapping("/{streamId}")
    public ResponseEntity<Boolean> deleteCctv(@PathVariable String streamId) {
        return ResponseEntity.ok(cctvservice.deleteCctv(streamId) == 1);
    }

    @PutMapping("")
    public ResponseEntity<List<TblCctv>> updateCctv(@RequestBody TblCctv newCctv) {
        return ResponseEntity.ok(cctvservice.updateCctv(newCctv));
    }



}
