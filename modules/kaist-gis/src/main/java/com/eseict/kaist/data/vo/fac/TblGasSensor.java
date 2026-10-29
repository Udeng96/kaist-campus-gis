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
@Table(name = "tbl_gas_sensor", schema = "fac")
public class TblGasSensor {

    @Id
    @Column(name =  "sensor_id")
    private String sensorId;

    @Column(name = "sensor_nm")
    private String sensorNm;

    @Column(name = "sensor_building")
    private String sensorBuilding;
}
