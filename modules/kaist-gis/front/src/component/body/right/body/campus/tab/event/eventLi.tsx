import EventLiSearch from "./eventLiSearch.tsx";
import {useRightStore} from "../../../../../../../store/zustand/right.ts";
import {RIGHT_MENU_EVENT} from "../../../../../../../data/const/common.ts";
import EventLiCnt from "./eventLiCnt.tsx";
import EventLiResult from "./eventLiResult.tsx";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useEffect, useState} from "react";
import moment from "moment";
import {
    header,
    headerList,
    setBody,
    setBuildingNm, setDtmForm,
    setEventTypeNm, setSensorNm,
    TITLE_STYLE
} from "../../../../../../../data/const/excel.ts";
import * as XLSX from "xlsx-js-style";
import {useMainStore} from "../../../../../../../store/zustand/main.ts";


const EventLi = () => {

    const {activeEventList, activeRightMenu, setActiveEventList, activeEventStartDtm, activeEventEndDtm, setActiveEventStartDtm, setActiveEventEndDtm} = useRightStore(useShallow((state)=> ({
        activeEventList : state.activeEventList,
        activeRightMenu : state.activeRightMenu,
        setActiveEventList : state.actions.setActiveEventList,
        activeEventStartDtm : state.activeEventStartDtm,
        activeEventEndDtm : state.activeEventEndDtm,
        setActiveEventStartDtm : state.actions.setActiveEventStartDtm,
        setActiveEventEndDtm : state.actions.setActiveEventEndDtm,
    })));
    const {eventList, activeBuilding, buildingList} = useLeftStore(useShallow((state)=> ({
        eventList : state.eventList,
        activeBuilding : state.activeBuilding,
        buildingList : state.buildingList
    })));

    const {sensorList} = useMainStore(useShallow((state)=> ({
        sensorList : state.sensors
    })));

    const [excelBodyList, setExcelBodyList] = useState<{
        s: { alignment: { horizontal: string, vertical: string }, font: { sz: string, bold: boolean } },
        t: string,
        v: string
    }[][]>([]);

    const handleExcelBtn = () => {
        if (activeEventList.length > 0) {
            let bodies: {
                s: { alignment: { horizontal: string, vertical: string }, font: { sz: string, bold: boolean } },
                t: string,
                v: string
            }[][] = []
            activeEventList.map((item, index) => {
                let body: {
                    s: { alignment: { horizontal: string, vertical: string }, font: { sz: string, bold: boolean } },
                    t: string,
                    v: string
                }[] = [];

                headerList.forEach((order) => {
                    body.push(setBody(order, "s"));
                })
                let bodyItem: {
                    s: { alignment: { horizontal: string, vertical: string }, font: { sz: string, bold: boolean } },
                    t: string,
                    v: string
                }[] = [];

                bodyItem.push(setBody((index + 1).toString(), "s"));
                bodyItem.push(setBody(setEventTypeNm(item.type), "s"));
                bodyItem.push(setBody(item.mappBuildingId, "s"));
                bodyItem.push(setBody(setBuildingNm(buildingList, item.mappBuildingId), "s"));
                bodyItem.push(setBody(setSensorNm(item.type,sensorList, item.mappSensorId), "s"));
                bodyItem.push(setBody(setDtmForm(item.outbDtm), "s"));
                bodyItem.push(setBody(setDtmForm(item.clrDtm), "s"));

                bodies.push(bodyItem);

            })

            setExcelBodyList(bodies);
        }else{
            setExcelBodyList([]);
            alert("엑셀 다운로드를 실패하였습니다.");
        }
    }

    useEffect(() => {
        if(activeBuilding && excelBodyList.length>0){
            // @ts-ignore
            const wb = XLSX.utils.book_new();
            const title = [{
                v: `${activeBuilding.id} 이벤트 발생 리스트 (${activeEventList.length.toLocaleString()})`,
                t: "s",
                s: {...TITLE_STYLE, alignment: {horizontal: "left"}}
            }]

            let body = [];
            body.push(header);
            body.push(...excelBodyList);

            // @ts-ignore
            const ws = XLSX.utils.aoa_to_sheet([title, ...body]);
            const header_width_list = headerList.map((_,index) => (index===0 ? {wch: 20} : {wch: 40}))
            const body_width_list = excelBodyList.map((_) => ({hpx: 20}))
            ws["!cols"] = [...header_width_list];
            ws["!rows"] = [{hpx: 30}, {hpx: 30}, ...body_width_list];
            ws["!merges"] = [{s: {r: 0, c: 0}, e: {r: 0, c: headerList.length - 1}}];
            // @ts-ignore
            XLSX.utils.book_append_sheet(wb, ws, ` 이벤트 리스트 이력`);

            XLSX.writeFile(wb,`${activeBuilding.name} 건물 이벤트 이력 - ${moment().format('YYYYMMDD')}.xlsx`, {bookType: "xlsx"});

            setExcelBodyList([]);
        }
    }, [excelBodyList]);


    useEffect(() => {
        if(eventList.length >0){
            if(activeBuilding){
                let filterEvents = eventList.filter((event)=> event.mappBuildingId.includes(activeBuilding.id));
                filterEvents = filterEvents.filter((event)=> (Number(event.outbDtm.substring(0,8))<= Number(activeEventEndDtm)) && (Number(event.outbDtm.substring(0,8))>= Number(activeEventStartDtm)));
                setActiveEventList(filterEvents);
            }else{
                setActiveEventList([]);
            }
        }else{
            setActiveEventList([]);
        }
    }, [activeBuilding, eventList, activeEventStartDtm, activeEventEndDtm]);


    useEffect(() => {
        setActiveEventStartDtm(moment().format('YYYYMMDD'))
        setActiveEventEndDtm(moment().format('YYYYMMDD'))
    }, [activeBuilding]);




    return(
        <li className={`tab__item tab__item--event ${activeRightMenu.cd === RIGHT_MENU_EVENT.cd ? 'active' : ''}`}>
            <div className="content__sub-head">
                <h2 className="content__sub-title">이벤트 발생 현황</h2>
                <button type="button" className="btn btn-normal btn-excel" onClick={handleExcelBtn}>엑셀 다운</button>
            </div>
            <EventLiSearch/>
            <EventLiCnt eventList={activeEventList}/>
            <EventLiResult eventList={activeEventList}/>
        </li>
    )


}

export default EventLi