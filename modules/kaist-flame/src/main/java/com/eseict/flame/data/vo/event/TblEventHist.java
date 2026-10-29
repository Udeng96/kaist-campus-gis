package com.eseict.flame.data.vo.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_event_hist", schema = "event")
public class TblEventHist {

    @Id
    @Column(name = "seqn")
    private String seqn;

    @Column(name = "type")
    private String type;

    @Column(name = "outb_dtm")
    private String outbDtm;

    @Column(name = "clr_dtm")
    private String clrDtm;

    @Column(name = "mapp_building_id")
    private String mappBuildingId;

    @Column(name = "mapp_cctv_id")
    private String mappCctvId;

    @Column(name = "mapp_sensor_id")
    private String mappSensorId;


}
