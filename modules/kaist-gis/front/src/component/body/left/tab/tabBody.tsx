import CampusMenu from "./menu/campus/campusMenu.tsx";
import Event from "./menu/event/event.tsx";
import {useLeftStore} from "../../../../store/zustand/left.ts";
import {LEFT_MENU_EVENT} from "../../../../data/const/common.ts";
import {useQuery} from "@tanstack/react-query";
import {fetchEvents} from "../../../../data/api/left/leftApi.ts";
import {useEffect} from "react";
import {useShallow} from "zustand/react/shallow";

const TabBody = () => {

    const {activeTab, lastEventParam, setEventList} = useLeftStore(useShallow((state) => ({
        activeTab : state.activeTab,
        lastEventParam : state.lastEventParam,
        setEventList: state.actions.setEventList,
    })));

    // 이벤트 목록은 항상 서버 스냅샷(오늘 + 현재 필터)으로 유지한다.
    // 5분 주기 폴링으로 갱신되므로 조회를 누르지 않아도 자정이 지나면 오늘 것만 남고,
    // WS 이벤트가 클라이언트에 무한 누적되지 않는다. (지도 마커/카운트도 같은 목록을 읽음)
    const {data: eventRes} = useQuery({
            queryKey: ['mainEvents', lastEventParam],
            queryFn: () => fetchEvents({eventArea : lastEventParam ? lastEventParam.eventArea : '', eventType : lastEventParam ? lastEventParam.eventType : ''}),
            enabled : lastEventParam !== null,
            refetchInterval : 5 * 60 * 1000,
            refetchIntervalInBackground : true,
        },
    )

    useEffect(() => {
        if (eventRes && eventRes.data) {
            setEventList(eventRes.data);
        }
    }, [eventRes]);

    return (
        <div className="content__body">
            <ul>
                {
                    activeTab.cd !== LEFT_MENU_EVENT.cd && <CampusMenu/>
                }
                {
                    activeTab.cd === LEFT_MENU_EVENT.cd && <Event/>
                }
            </ul>
        </div>

    )
}

export default TabBody
