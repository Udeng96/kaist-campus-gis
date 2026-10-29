package com.eseict.flame.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FireEventDto {
    private int type;
    private long timestamp;
    private long rowid;
    private List<Integer> devices;
}
