package com.eseict.ibsmodule.service.stream;

import com.eseict.ibsmodule.data.dto.RxState;
import com.eseict.ibsmodule.service.packet.PacketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;

@RequiredArgsConstructor
@Slf4j
public class NMuxStreamService implements TcpStreamService {

    private final PacketHandler packetHandler;

    private static final int PACKET_SIZE = 72;
    private static final byte START_BYTE = 'A';

    // 이 부분 때문에 여기서는 @Component를 사용할 수 없다.
    // @Component를 사용하게 되면 Singleton 구조가 되는데 이렇게 되면
    // 상태가 변경되면 모든 변수가 공유하게 된다.
    // 각각의 객체마다 다른 값을 가져야하는데, 하나가 바뀌면 다른 모든 객체에도 다 바뀐 값이 전달된다.

    RxState state = RxState.WAIT_A; // 초기 상태는 A를 기다리는 상태
    ByteArrayOutputStream packetBuffer = new ByteArrayOutputStream(PACKET_SIZE);


    @Override
    public void onBytes(byte[] buffer, int length) {
        for (int i = 0; i < length; i++) {
            byte b = buffer[i];

            switch (state) {
                // packet 시작
                // A일 경우 버리지도, 그렇다고 저장하지도 않는다.
                case WAIT_A:
                    if (b == START_BYTE) {
                        // A는 계속 덮어쓴다 → 마지막 A만 살아남음 (A가 몇개가 들어오든간에, 패킷이 시작할 때 가장 마지막의 A만 사용)
                        packetBuffer.reset();
                        // 버퍼에 일단 하나 저장한다.
                        packetBuffer.write(b);
                        // 초기 말고 A가 들어온 상태로 변경
                        state = RxState.COLLECT;
                    }
                    // A가 아니면 그냥 버림(시작이 A가 아니면 패킷이 시작이 안되기 때문이다.)
                    break;

                // 이미 A가 쌓여있는데
                case COLLECT:

                    // 이미 A가 있는데 또 A가 들어왔을 경우
                    if (packetBuffer.size() == 1 && b == START_BYTE) {
                        // 이전 A는 버리고, 지금 A를 새로운 시작으로
                        packetBuffer.reset();
                        packetBuffer.write(b);
                        break;
                    }

                    // 일반 바이트 수집
                    packetBuffer.write(b);

                    // 72바이트가 찼으면 패킷 완성
                    if (packetBuffer.size() == PACKET_SIZE) {
                        log.info("72 PACKET ORIGIN BUFFER - {}", packetBuffer);
                        packetHandler.handle(packetBuffer.toByteArray());

                        packetBuffer.reset();
                        state = RxState.WAIT_A;
                    }
                    break;
            }
        }
    }
}
