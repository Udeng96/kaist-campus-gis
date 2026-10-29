package com.eseict.kaist.service.event;

import com.eseict.kaist.data.vo.event.TblEventHist;
import com.eseict.kaist.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<TblEventHist> getEvents(String eventArea, String eventType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "outbDtm");


        if (Strings.isEmpty(eventArea) && Strings.isEmpty(eventType)) {
            return getTodayEvents(eventRepository.findAll(sort));
        } else {
            if (Strings.isEmpty(eventArea) && Strings.isNotEmpty(eventType)) {
                return getTodayEvents(eventRepository.findByType(eventType, sort));
            } else if (Strings.isEmpty(eventType) && Strings.isNotEmpty(eventArea)) {
                return  getTodayEvents(eventRepository.findByMappBuildingIdLike(eventArea + "%", sort));
            } else {
                return getTodayEvents(eventRepository.findByMappBuildingIdLikeAndType(eventArea + "%", eventType, sort));
            }
        }
    }

    public List<TblEventHist> getTodayEvents(List<TblEventHist> allDayHists){
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return allDayHists.stream().filter(item-> item.getOutbDtm().startsWith(today)).collect(Collectors.toList());
    }

    public List<TblEventHist> getLastEvents(String startDtm, String endDtm, String eventArea, String eventType) {
        Sort sort = Sort.by(Sort.Direction.DESC, "outbDtm");
        if (Strings.isEmpty(eventArea) && Strings.isEmpty(eventType)) {
            return eventRepository.findByOutbDtmBetween(startDtm, endDtm, sort);
        } else {
            if (Strings.isEmpty(eventArea) && Strings.isNotEmpty(eventType)) {
                return eventRepository.findByType(eventType, sort);
            } else if (Strings.isEmpty(eventType) && Strings.isNotEmpty(eventArea)) {
                return eventRepository.findByMappBuildingIdLikeAndOutbDtmBetween(eventArea + "%", startDtm, endDtm, sort);
            } else {
                return eventRepository.findByMappBuildingIdLikeAndTypeAndOutbDtmBetween(eventArea + "%", eventType, startDtm, endDtm, sort);
            }
        }
    }

    public List<TblEventHist> getBuildingEvents(String startDtm, String endDtm, String buildId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "outbDtm");
        return eventRepository.findByMappBuildingIdAndOutbDtmBetween(buildId, startDtm, endDtm, sort);
    }

    public TblEventHist savePassiveEvent(TblEventHist events) {
        String eventId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        events.setSeqn(eventId);
        TblEventHist saveResult = eventRepository.save(events);
        if (saveResult.getSeqn().equals(events.getSeqn())) {
            return saveResult;
        } else {
            log.info("수동 이벤트 등록에 실패했습니다.");
            return null;
        }
    }

    public String updateClrDtm(String seqn, String clrDtm) {
        int updateResult = eventRepository.updateClrDtm(clrDtm,seqn);
        if (updateResult > 0) {
            log.info("수동 이벤트 종료에 성공했습니다.");
            return "SUCCESS_RESPONSE";

        } else {
            log.info("수동 이벤트 종료에 실패하였습니다.");
            return "FAILURE_RESPONSE";
        }
    }
}
