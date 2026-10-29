package com.eseict.kaist.data.vo.fac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_fire_sensor", schema = "fac")
public class TblFireSensor {

    @Id
    @Column(name = "sensor_id")
    private String sensorId;

    @Column(name = "sensor_panel")
    private String sensorPanel;

    @Column(name = "sensor_sub_panel")
    private String sensorSubPanel;

    @Column(name = "sensor_loop")
    private String sensorLoop;

    @Column(name = "sensor_repeater")
    private String sensorRepeater;

    @Column(name = "sensor_circuit")
    private Integer sensorCircuit;

    @Column(name = "sensor_circuit_nm")
    private String sensorCircuitNm;

    @Column(name = "sensor_building")
    private String sensorBuilding;

    @Column(name = "sensor_area")
    private String sensorArea;

}
