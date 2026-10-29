import {useFireStomp} from "../stomp/useFireStomp.ts";
import {COMMON_CONTEXT} from "../../data/api/common/commonUrl.ts";
import {useShallow} from "zustand/react/shallow";
import {useMainStore} from "../../store/zustand/main.ts";
import type {EVENT_TYPE} from "../../data/interface/leftInterface.tsx";

export const UseFireWebSocketConn = () => {
    const {setActiveWsEvent} = useMainStore(useShallow((state) => ({
        setActiveWsEvent: state.actions.setActiveWsEvent,
    })))

    useFireStomp(COMMON_CONTEXT.WEB_SOCKET.FIRE, [
        {
            topic: '/event/fire',
            handler: (message: EVENT_TYPE) => {
                console.log("화재 이벤트 수신 ::", message);

                if(Array.isArray(message)){
                    setActiveWsEvent(message[0]);
                }else{
                    setActiveWsEvent(message);

                }
            }
        }
    ])
}