package com.eseict.ibsmodule.tcp.simul;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TcpEventSimulator {


    public static InputStream createSimulatedStream() throws Exception {
        ByteArrayOutputStream streamData = new ByteArrayOutputStream();

        // 1️⃣ AAA (KeepAlive)
        streamData.write("AAA".getBytes(StandardCharsets.US_ASCII));

        // 2️⃣ 실제 72바이트 NMUX 패킷
        String hex = "41060C041A112D0E00303130313030303131004235442D4EB5BFC0FC0000000000000000000000502E414C2E444554000000000000000000000000000000C7D800000000000000";
        streamData.write(hexStringToByteArray(hex));

        // 3️⃣ 스트림 리턴
        return new ByteArrayInputStream(streamData.toByteArray());
    }

    private static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("\\s+", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
        return data;
    }
}
