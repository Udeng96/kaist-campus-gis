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
@Table(name = "fire_log", schema = "event")
public class FireLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "detected_at", nullable = false)
    private OffsetDateTime detectedAt;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public FireLog(Long eventId, String sensorId, Long buildingId, int status, OffsetDateTime detectedAt) {
        this.eventId = eventId;
        this.sensorId = sensorId;
        this.buildingId = buildingId;
        this.status = status;
        this.detectedAt = detectedAt;
    }
}
