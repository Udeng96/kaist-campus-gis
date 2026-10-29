import EventCntBox from "./eventCntBox.tsx";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {EVENT_TYPE_FIRE, EVENT_TYPE_FLAME, EVENT_TYPE_GAS} from "../../../../../../../data/const/common.ts";

const EventCnt = () => {

    const eventList = useLeftStore((state)=> state.eventList)
    return(
        <div className="event__frame">
            <h2 className="content__sub-title">오늘의 이벤트 요약</h2>
            <ul>
                <EventCntBox eventNm={"가스"} cnt={eventList.filter((eventItem)=> eventItem.type.includes(EVENT_TYPE_GAS.cd)).length}/>
                <EventCntBox eventNm={"불꽃"} cnt={eventList.filter((eventItem)=> eventItem.type.includes(EVENT_TYPE_FLAME.cd)).length}/>
                <EventCntBox eventNm={"화재"} cnt={eventList.filter((eventItem)=> eventItem.type.includes(EVENT_TYPE_FIRE.cd)).length}/>
            </ul>
        </div>
    )

}

export default EventCnt
