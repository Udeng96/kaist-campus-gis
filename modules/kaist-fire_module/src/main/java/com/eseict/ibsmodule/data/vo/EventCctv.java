package com.eseict.ibsmodule.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_cctv", schema = "event")
public class EventCctv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "cctv_id", nullable = false)
    private Long cctvId;

    public EventCctv(Long eventId, Long cctvId) {
        this.eventId = eventId;
        this.cctvId = cctvId;
    }
}
