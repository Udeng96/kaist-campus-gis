import {useEffect, useState} from "react";
import {useMap} from "react-leaflet";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useGisStore} from "../../../../../../../store/zustand/gis.ts";
import {useFavoriteStore} from "../../../../../../../store/zustand/favorite.ts";
import {CCTV_TYPE_WHOLE, LEFT_MENU_FAVORITE} from "../../../../../../../data/const/common.ts";
import type {CCTV_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";
import L from "leaflet";
import ReactDOMServer from "react-dom/server";
import {DataMarker} from "../../../../../../../data/interface/ccomonInterface.ts";

const FavoriteCctvMarker = () => {


    const [hoverCctv, setHoverCctv] = useState<string>("");

    const map = useMap();
    const {
        cctvList,
        selectCctvList,
        activeTab,
        setSelectCctvList,
        activePatrol,
        activeBuilding,
        activeCctvType,
    } = useLeftStore(useShallow((state) => ({
        cctvList: state.cctvList,
        selectCctvList: state.selectCctvList,
        activeTab: state.activeTab,
        setSelectCctvList: state.actions.setSelectCctvList,
        activePatrol: state.activePatrol,
        activeBuilding : state.activeBuilding,
        activeCctvType : state.activeCctvType,
    })))

    const {favorites} = useFavoriteStore(useShallow((state) => ({
        favorites: state.favorites,
    })))
    const {activeZoom, cctvIconShow} = useGisStore(useShallow((state) => ({
        activeZoom : state.activeZoom,
        cctvIconShow: state.cctvIconShow,
    })));

    useEffect(() => {
        map.eachLayer((l) => {
            if (l.options.pane === 'favoriteCctvMarker') {
                map.removeLayer(l);
            }
        })

        if (activeTab.cd === LEFT_MENU_FAVORITE.cd) {
            if (activePatrol === null) {
                if (cctvIconShow) {
                    let filterCctvs = cctvList.filter((cctv) => activeCctvType.cd === CCTV_TYPE_WHOLE.cd || cctv.plcType === activeCctvType.id);
                    filterCctvs = filterCctvs.filter((cctv)=> favorites.has(cctv.streamId));
                    filterCctvs.forEach((cctv) => {
                        const activeMarker = makeMarker(cctv, activeZoom >= 11);
                        if(activeBuilding === null){
                            activeMarker.on('click', markerClick);
                        }
                        map.addLayer(activeMarker.getLeafletMarker())
                    })
                }
            }
        }
    }, [cctvList, selectCctvList, activeTab, activePatrol, activeZoom, activeBuilding]);
    const makeMarker = (cctvInfo: CCTV_TYPE, zoomOut : boolean) => {
        const marker = new DataMarker(
            [Number(cctvInfo.ycrdnt), Number(cctvInfo.xcrdnt)], {
                pane: 'favoriteCctvMarker',
                data: cctvInfo,
                icon: renderIcon(cctvInfo, zoomOut),
                interactive:true,
            });

        marker.on('mouseover', ()=> setHoverCctv(cctvInfo.streamId));
        marker.on('mouseout', () => setHoverCctv(""));
        return marker;
    }

    const renderIcon = (cctvInfo : CCTV_TYPE, zoomOut: boolean) => {
        return L.divIcon({
            className: "cctv_zoom_icon",
            html: ReactDOMServer.renderToString(
                <div onMouseOver={() => setHoverCctv(cctvInfo.streamId)} onMouseOut={() => setHoverCctv("")}>
                    <button className={`gis__icon cctv cctv-${cctvInfo.plcType === '1' ? 'in' : cctvInfo.plcType === '2' ? 'out' : 'flame'} ${zoomOut ? 'zoom' : ''}`}
                            style={{zIndex: "1003"}} />
                    {
                        hoverCctv !== "" && hoverCctv === cctvInfo.streamId &&
                        <div className={"tooltip"} style={{zIndex: "1010"}}>{cctvInfo.cctvNm}</div>
                    }
                </div>

            ),
            iconAnchor: [2, 12], // 중앙 하단에 기준점
            iconSize: [24, 24]
        });
    }

    const markerClick = (e: any) => {
        const markerInfo = e.target.options.data;

        let newSelectList = [...selectCctvList];

        const alreadyIds = newSelectList.map((item)=> item.streamId);
        if(activeBuilding === null){
            if(!alreadyIds.includes(markerInfo.streamId)){
                setSelectCctvList([...newSelectList, markerInfo]);
            }
        }
    };

    return null;

}

export default FavoriteCctvMarker