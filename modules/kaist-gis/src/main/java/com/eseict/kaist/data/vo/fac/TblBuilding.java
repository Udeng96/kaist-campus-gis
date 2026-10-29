package com.eseict.kaist.data.vo.fac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tbl_building", schema = "fac")
public class TblBuilding {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "area")
    private String area;

    @Column(name = "note")
    private String note;

    @Column(name = "favorite_yn")
    private String favoriteYn;

    @Column(name = "xcrdnt")
    private String xcrdnt;

    @Column(name = "ycrdnt")
    private String ycrdnt;
}
