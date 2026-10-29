import {useMainStore} from "../../../../store/zustand/main.ts";

const Report = () => {

    const weather = useMainStore((state)=> state.weather);
    return(
        <div className="marquee">
            <p>{weather && weather.reportContent ? weather.reportContent : '특보 없음'}</p>
        </div>
    )

}

export default Report