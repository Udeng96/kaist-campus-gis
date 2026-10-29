package com.eseict.ibsmodule.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event", schema = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "cleared_at")
    private OffsetDateTime clearedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Event(String type, Long buildingId, String sensorId, OffsetDateTime occurredAt) {
        this.type = type;
        this.buildingId = buildingId;
        this.sensorId = sensorId;
        this.occurredAt = occurredAt;
    }
}
