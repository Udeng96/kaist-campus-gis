package com.eseict.kaist.data.vo.publi;

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
@Table(name = "tbl_weather", schema = "public")
public class TblWeather {

    @Id
    @Column(name = "reg_dtm")
    private String regDtm;

    @Column(name = "upd_dtm")
    private String updDtm;

    @Column(name = "temp")
    private String temp;

    @Column(name = "sky")
    private String sky;

    @Column(name = "dust")
    private String dust;

    @Column(name = "ultra_dust")
    private String ultraDust;

    @Column(name = "pty")
    private String pty;

    @Column(name = "sky_cd")
    private String skyCd;

    @Column(name = "pty_cd")
    private String ptyCd;

    @Column(name = "report_content")
    private String reportContent;


    public TblWeather(String temp, String sky, String pty, String skyCd, String ptyCd) {
        this.temp = temp;
        this.sky = sky;
        this.pty = pty;
        this.skyCd = skyCd;
        this.ptyCd = ptyCd;
    }
}
