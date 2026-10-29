package com.eseict.ibsmodule.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class Util {

    private static final ZoneOffset KST = ZoneId.of("Asia/Seoul").getRules().getOffset(java.time.Instant.now());

    /**
     * NMux 패킷의 datetime 바이트(6바이트: YY MM DD HH mm ss)를 OffsetDateTime으로 변환
     */
    public OffsetDateTime formatOutbDtm(byte[] datetimeBytes) {
        int year = 2000 + (datetimeBytes[0] & 0xFF);
        int month = datetimeBytes[1] & 0xFF;
        int day = datetimeBytes[2] & 0xFF;
        int hour = datetimeBytes[3] & 0xFF;
        int minute = datetimeBytes[4] & 0xFF;
        int second = datetimeBytes[5] & 0xFF;

        return LocalDateTime.of(year, month, day, hour, minute, second).atOffset(KST);
    }

    public static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }
}
