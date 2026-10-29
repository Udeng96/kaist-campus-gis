import Cctv from "./cctv/cctv.tsx";
import EventLi from "./event/eventLi.tsx";

const RightTabBody = () => {

    return (
        <div className={"content__body"}>
            <ul>
                <Cctv/>
                <EventLi/>
            </ul>
        </div>
    )
}

export default RightTabBody