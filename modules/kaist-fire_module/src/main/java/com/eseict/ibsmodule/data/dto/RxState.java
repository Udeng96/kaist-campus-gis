package com.eseict.ibsmodule.data.dto;

public enum RxState {
    WAIT_A,    // 패킷 시작 A를 찾는 상태
    COLLECT   // A로 시작한 72바이트 패킷을 조립 중
}
