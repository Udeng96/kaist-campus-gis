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
@Table(name = "cctv", schema = "fac")
public class Cctv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cctv_id")
    private Long cctvId;

    @Column(name = "stream_id", nullable = false, unique = true)
    private String streamId;

    @Column(name = "cctv_nm")
    private String cctvNm;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "port")
    private Integer port;

    @Column(name = "health")
    private String health;

    @Column(name = "rtsp_01")
    private String rtsp01;

    @Column(name = "rtsp_02")
    private String rtsp02;

    @Column(name = "id")
    private String id;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "plc_type")
    private String plcType;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "x_coord")
    private BigDecimal xCoord;

    @Column(name = "y_coord")
    private BigDecimal yCoord;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
