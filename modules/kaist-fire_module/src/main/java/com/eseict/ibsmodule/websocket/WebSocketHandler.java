package com.eseict.ibsmodule.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class WebSocketHandler {
    // ✅ 클라이언트 연결 시
    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        log.info("[FIRE] WebSocket Client Connected:");
    }

    // ✅ STOMP CONNECT 처리 완료 시
    @EventListener
    public void handleConnectedEvent(SessionConnectedEvent event) {
        log.info("[FIRE] STOMP Connected: sessionId={}", event.getMessage().getHeaders().get("simpSessionId"));
    }

    // ✅ 클라이언트 구독 이벤트
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        log.info("[FIRE] Client Subscribed");
    }

    // ✅ 클라이언트 연결 끊김
    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        log.warn("[FIRE] WebSocket Disconnected");
    }
}
