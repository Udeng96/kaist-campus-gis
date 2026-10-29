package com.eseict.flame.data.vo.fac;


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
@Table(name = "tbl_cctv", schema = "fac")
public class TblCctv {

    @Id
    @Column(name = "stream_id")
    private String streamId;

    @Column(name = "cctv_nm")
    private String cctvNm;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "health")
    private String heath;

    @Column(name = "rtsp_01")
    private  String rtsp01;

    @Column(name = "rtsp_02")
    private String rtsp02;

    @Column(name = "id")
    private String id;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "port")
    private int port;

    @Column(name = "plc_type")
    private String plcType;

    @Column(name = "building")
    private String building;

    @Column(name = "xcrdnt")
    private String xcrdnt;

    @Column(name = "ycrdnt")
    private String ycrdnt;

    @Column(name = "favorite_yn")
    private String favoriteYn;
}
