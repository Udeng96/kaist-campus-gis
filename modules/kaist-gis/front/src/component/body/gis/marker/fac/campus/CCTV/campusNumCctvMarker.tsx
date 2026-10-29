import {useMap} from "react-leaflet";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useGisStore} from "../../../../../../../store/zustand/gis.ts";
import {useEffect} from "react";
import {CCTV_TYPE_WHOLE, LEFT_MENU_CAMPUS, LEFT_MOD_TAB} from "../../../../../../../data/const/common.ts";
import type {CCTV_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";
import {DataMarker} from "../../../../../../../data/interface/ccomonInterface.ts";
import L from "leaflet";
import ReactDOMServer from "react-dom/server";
import {useMainStore} from "../../../../../../../store/zustand/main.ts";

const CampusNumCctvMarker = () => {
    const map = useMap();
    const {
        activeCctvType,
        activeBuilding,
        activeBuildingCctvs,
        selectCctvList,
        activeTab,
        setSelectCctvList,
        activePatrol
    } = useLeftStore(useShallow((state) => ({
        activeCctvType : state.activeCctvType,
        activeBuilding: state.activeBuilding,
        activeBuildingCctvs: state.activeBuildingCctvs,
        selectCctvList: state.selectCctvList,
        activeTab: state.activeTab,
        setSelectCctvList: state.actions.setSelectCctvList,
        activePatrol: state.activePatrol,
    })))

    const {activeZoom, cctvIconShow} = useGisStore(useShallow((state) => ({
        activeZoom: state.activeZoom,
        cctvIconShow: state.cctvIconShow,
    })));

    const activeMod = useMainStore((state)=> state.activeMod);

    useEffect(() => {
        map.eachLayer((l) => {
            if (l.options.pane === 'campusNumCctvMarker') {
                map.removeLayer(l);
            }
        })

        if (activeTab.cd === LEFT_MENU_CAMPUS.cd && activeMod.cd === LEFT_MOD_TAB.cd) {
            if (activePatrol === null) {
                if (cctvIconShow) {
                    if(activeBuilding === null){
                        console.log("selectCctvList:", selectCctvList);
                        const filterCctvs = selectCctvList.filter((cctv) => activeCctvType.cd === CCTV_TYPE_WHOLE.cd || cctv.plcType === activeCctvType.id);

                        filterCctvs.forEach((cctv, idx) => {
                            const activeMarker = makeMarker(cctv, idx);
                            map.addLayer(activeMarker.getLeafletMarker())
                        })
                    }else{
                        const filterCctvs = activeBuildingCctvs.filter((cctv) => activeCctvType.cd === CCTV_TYPE_WHOLE.cd || cctv.plcType === activeCctvType.id);

                        filterCctvs.forEach((cctv, idx) => {
                            const activeMarker = makeMarker(cctv, idx);
                            map.addLayer(activeMarker.getLeafletMarker())
                        })
                    }

                }
            }
        }
    }, [activeCctvType, activeTab, activeBuilding, activeBuildingCctvs, selectCctvList, activePatrol, activeZoom, cctvIconShow]);


    const makeMarker = (cctvInfo: CCTV_TYPE, idx: number) => {
        const marker = new DataMarker(
            [Number(cctvInfo.ycrdnt), Number(cctvInfo.xcrdnt)], {
                pane: 'campusNumCctvMarker',
                data: cctvInfo,
                icon: renderIcon(cctvInfo, idx),
                interactive: true,
            });

        marker.on('click', markerClick);
        return marker;
    }

    const renderIcon = (cctvInfo: CCTV_TYPE, idx: number) => {
        return L.divIcon({
            className: "cctv_num_icon",
            html: ReactDOMServer.renderToString(
                <button
                    className={`gis__icon cctv cctv-${cctvInfo.plcType === '1' ? 'in' : cctvInfo.plcType === '2' ? 'out' : 'flame'} num `}
                    style={{zIndex: "1003"}}><span>{idx + 1}</span></button>),
            iconAnchor: [6, 20], // 중앙 하단에 기준점
            iconSize: [24, 24]
        });
    }

    const markerClick = (e: any) => {
        const markerInfo = e.target.options.data;
        let newSelectList = [...selectCctvList];

        const alreadyIds = newSelectList.map((item)=> item.streamId);

        if(alreadyIds.includes(markerInfo.streamId)){
            let filterCctvs = newSelectList.filter((item)=> item.streamId !== markerInfo.streamId);
            setSelectCctvList(filterCctvs);
        }
    }

    return null;
}

export default CampusNumCctvMarker