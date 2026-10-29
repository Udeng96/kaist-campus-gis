package com.eseict.ibsmodule.data.dto;

public enum EventType {
    ALL(10),
    FIRE(6);

    private final int code;

    EventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
