import {COMMON_CONTEXT} from "../../data/api/common/commonUrl.ts";
import {useMainStore} from "../../store/zustand/main.ts";
import {useShallow} from "zustand/react/shallow";
import type {EVENT_TYPE} from "../../data/interface/leftInterface.tsx";
import {useFlameStomp} from "../stomp/useFlameStomp.ts";

export const UseFlameWebSocketConn = () => {

    const {setActiveWsEvent} = useMainStore(useShallow((state) => ({
        setActiveWsEvent: state.actions.setActiveWsEvent,
    })))

    useFlameStomp(COMMON_CONTEXT.WEB_SOCKET.FLAME, [
        {
            topic: '/event/flame',
            handler: (message: EVENT_TYPE) => {
                console.log("불꽃 감지 이벤트 수신 ::", message);
                setActiveWsEvent(message);
            }
        }
    ])
}

