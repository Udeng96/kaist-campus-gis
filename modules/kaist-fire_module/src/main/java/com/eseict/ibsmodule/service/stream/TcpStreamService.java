package com.eseict.ibsmodule.service.stream;

public interface TcpStreamService {
    void onBytes(byte[] buffer, int length);
}
