package com.eseict.kaist.data.vo.event;


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
@Table(name = "tbl_event_patrol", schema = "event")
public class TblEventPatrol {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "reg_dtm")
    private String regDtm;

    @Column(name = "cctv_mapp_id")
    private String cctvMappId;
}
