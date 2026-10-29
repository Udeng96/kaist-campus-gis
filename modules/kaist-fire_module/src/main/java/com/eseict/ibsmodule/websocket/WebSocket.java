package com.eseict.ibsmodule.websocket;

import com.eseict.ibsmodule.data.dto.FireEventMessage;
import com.eseict.ibsmodule.data.vo.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToWeb(Event event) {
        FireEventMessage message = FireEventMessage.from(event);
        messagingTemplate.convertAndSend("/event/fire", message);
        log.info("FIRE EVENT SENT - eventId:{}, buildingId:{}", message.getEventId(), message.getBuildingId());
    }
}
