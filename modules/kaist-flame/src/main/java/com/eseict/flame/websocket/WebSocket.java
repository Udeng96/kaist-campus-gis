package com.eseict.flame.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocket {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToWeb(Object payload) {
        messagingTemplate.convertAndSend("/event/flame", payload);
        log.info("불꽃 감지 이벤트 웹으로 전송됨 ");
    }

}
