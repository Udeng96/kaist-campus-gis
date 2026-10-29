package com.eseict.ibsmodule.service.packet;

import com.eseict.ibsmodule.data.dto.EventType;
import com.eseict.ibsmodule.service.event.ParserHandler;
import com.eseict.ibsmodule.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class EventPacketHandler implements PacketHandler {

    private final ParserHandler parserHandler;
    private final Util util;
    private static final Logger fireEventLogger = LoggerFactory.getLogger("eventLogger");
    private static final Charset EUC_KR = Charset.forName("EUC-KR");

    @Override
    public void handle(byte[] packet) {
        try {
            String rawHex = Util.bytesToHex(packet, packet.length);

            log.info("[PACKET -> HEX] :: {}", rawHex);
            fireEventLogger.info("[PACKET -> HEX] :: {}", rawHex);

            int lType = packet[1] & 0xFF;
            byte[] datetimeBytes = Arrays.copyOfRange(packet, 2, 8);
            int lOn = packet[8] & 0xFF;

            String panel = new String(Arrays.copyOfRange(packet, 9, 11), StandardCharsets.US_ASCII).trim();
            String area = new String(Arrays.copyOfRange(packet, 19, 40), EUC_KR).trim();
            String device = new String(Arrays.copyOfRange(packet, 40, 63), EUC_KR).trim();

            byte[] mapAddr = Arrays.copyOfRange(packet, 11, 19);

            OffsetDateTime occurredAt = util.formatOutbDtm(datetimeBytes);

            if (lType == EventType.FIRE.getCode()) {
                parserHandler.handleFireEvent(occurredAt, panel, area, device, mapAddr, lOn);
            } else if (lType == EventType.ALL.getCode()) {
                parserHandler.handleAllEvent(occurredAt, panel);
            } else {
                log.info("[UNKNOWN EVENT] - lType::{}", lType);
            }
        } catch (Exception e) {
            log.error("[PACKET] - ERROR ON PROCESSING PACKET", e);
            fireEventLogger.error("[PACKET] - ERROR ON PROCESSING PACKET", e);
        }
    }
}
