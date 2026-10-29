import {useMap} from "react-leaflet";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useGisStore} from "../../../../../../../store/zustand/gis.ts";
import {useFavoriteStore} from "../../../../../../../store/zustand/favorite.ts";
import {useEffect, useState} from "react";
import {CCTV_TYPE_WHOLE, LEFT_MENU_FAVORITE} from "../../../../../../../data/const/common.ts";
import ReactDOMServer from "react-dom/server";
import type {CCTV_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";
import {DataMarker} from "../../../../../../../data/interface/ccomonInterface.ts";
import L from "leaflet";

const FavoriteNumCctvMarker = () => {

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

    const {favorites} = useFavoriteStore(useShallow((state) => ({
        favorites: state.favorites,
    })))

    const {activeZoom, cctvIconShow} = useGisStore(useShallow((state) => ({
        activeZoom: state.activeZoom,
        cctvIconShow: state.cctvIconShow,
    })));

    const [filterList, setFilterList] = useState<CCTV_TYPE[]>([]);

    useEffect(() => {
        if(favorites.size > 0){
            if(activeBuilding === null){
                if(selectCctvList.length > 0){
                    let newCctvs = selectCctvList.filter((item)=> favorites.has(item.streamId));
                    setFilterList(newCctvs);
                }else{
                    setFilterList([])
                }
            }else{
                if(activeBuildingCctvs.length > 0){
                    const newCctvs = activeBuildingCctvs.filter((item)=> favorites.has(item.streamId));
                    setFilterList(newCctvs);
                }else{
                    setFilterList([]);
                }
            }
        }else{
            setFilterList([]);
        }
    }, [selectCctvList, activeBuildingCctvs,activeBuilding, favorites]);

    useEffect(() => {
        map.eachLayer((l) => {
            if (l.options.pane === 'favoriteNumCctvMarker') {
                map.removeLayer(l);
            }
        })

        if (activeTab.cd === LEFT_MENU_FAVORITE.cd) {
            if (activePatrol === null) {
                if (cctvIconShow) {
                    if(activeBuilding === null){
                        let filterCctvs = filterList.filter((cctv) => activeCctvType.cd === CCTV_TYPE_WHOLE.cd || cctv.plcType === activeCctvType.id);
                        filterCctvs = filterCctvs.filter((cctv)=> favorites.has(cctv.streamId))
                        filterCctvs.forEach((cctv, idx) => {
                            const activeMarker = makeMarker(cctv, idx);
                            map.addLayer(activeMarker.getLeafletMarker())
                        })
                    }else{

                        filterList.forEach((cctv, idx) => {
                            const activeMarker = makeMarker(cctv, idx);
                            map.addLayer(activeMarker.getLeafletMarker())
                        })
                    }
                }
            }
        }
    }, [activeCctvType, activeTab, activeBuilding, filterList, activePatrol, activeZoom, cctvIconShow]);

    const makeMarker = (cctvInfo: CCTV_TYPE, idx: number) => {
        const marker = new DataMarker(
            [Number(cctvInfo.ycrdnt), Number(cctvInfo.xcrdnt)], {
                pane: 'favoriteNumCctvMarker',
                data: cctvInfo,
                icon: renderIcon(cctvInfo, idx),
                interactive: true,
            });

        marker.on('click', markerClick);
        return marker;
    }

    const renderIcon = (cctvInfo: CCTV_TYPE, idx: number) => {
        return L.divIcon({
            className: "cctv_favorite_num_icon",
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

    return null
}

export default FavoriteNumCctvMarker