package com.eseict.ibsmodule.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FireEventMessage {

    private Long eventId;
    private String type;
    private Long buildingId;
    private String sensorId;
    private OffsetDateTime occurredAt;
    private OffsetDateTime clearedAt;

    public static FireEventMessage from(com.eseict.ibsmodule.data.vo.Event event) {
        return FireEventMessage.builder()
                .eventId(event.getId())
                .type(event.getType())
                .buildingId(event.getBuildingId())
                .sensorId(event.getSensorId())
                .occurredAt(event.getOccurredAt())
                .clearedAt(event.getClearedAt())
                .build();
    }
}
