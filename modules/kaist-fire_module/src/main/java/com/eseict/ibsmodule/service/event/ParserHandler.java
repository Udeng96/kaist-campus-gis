package com.eseict.ibsmodule.service.event;

import java.time.OffsetDateTime;

public interface ParserHandler {
    void handleAllEvent(OffsetDateTime occurredAt, String panel);
    void handleFireEvent(OffsetDateTime occurredAt, String panel, String area, String device, byte[] mapAddr, int lOn);
}
