package com.eseict.ibsmodule.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fire_sensor", schema = "fac")
public class FireSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, unique = true)
    private String sensorId;

    @Column(name = "panel")
    private String panel;

    @Column(name = "sub_panel")
    private String subPanel;

    @Column(name = "loop")
    private String loop;

    @Column(name = "repeater")
    private String repeater;

    @Column(name = "circuit")
    private BigDecimal circuit;

    @Column(name = "circuit_name")
    private String circuitName;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "area")
    private String area;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
