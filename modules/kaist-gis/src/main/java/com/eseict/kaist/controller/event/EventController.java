package com.eseict.kaist.controller.event;

import com.eseict.kaist.data.dto.CommonResponse;
import com.eseict.kaist.data.vo.event.TblEventHist;
import com.eseict.kaist.service.event.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.eseict.kaist.config.ApiConstant.API_PRODUCES;

@RestController
@RequestMapping(value = "/event", produces = API_PRODUCES)
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("")
    public ResponseEntity<List<TblEventHist>> getEvents(
            @RequestParam(value = "eventArea")String eventArea,
            @RequestParam(value = "eventType")String eventType
    ){
        return ResponseEntity.ok(eventService.getEvents(eventArea, eventType));
    }

    @GetMapping("/last")
    public ResponseEntity<List<TblEventHist>> getLastEvents(
            @RequestParam(value = "startDtm")String startDtm,
            @RequestParam(value = "endDtm")String endDtm,
            @RequestParam(value = "eventArea")String eventArea,
            @RequestParam(value = "eventType")String evetType) {
        return ResponseEntity.ok(eventService.getLastEvents(startDtm, endDtm, eventArea, evetType));
    }

    @GetMapping("/{buildId}")
    public ResponseEntity<List<TblEventHist>> getBuildingEvents(
            @RequestParam(value = "startDtm")String startDtm,
            @RequestParam(value = "endDtm")String endDtm,
        @PathVariable String buildId
    ){
        return ResponseEntity.ok(eventService.getBuildingEvents(startDtm, endDtm, buildId));
    }

    @PostMapping("/passive")
    public ResponseEntity<TblEventHist> savePassiveEvent(
            @RequestBody TblEventHist eventInfo
    ){
        return ResponseEntity.ok(eventService.savePassiveEvent(eventInfo));
    }

    @PutMapping("/clear/{seqn}")
    public ResponseEntity<String> updateClrDtm(
            @PathVariable String seqn,
            @RequestParam(value = "clrDtm")String clrDtm
    ){
        return ResponseEntity.ok(eventService.updateClrDtm(seqn,clrDtm));
    }


}
