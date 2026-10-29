package com.eseict.flame.data.vo.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_flame_log", schema = "event")
public class TblFlameLog {
    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "insert_dtm")
    private String insertDtm;

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(name = "building")
    private String building;

}
