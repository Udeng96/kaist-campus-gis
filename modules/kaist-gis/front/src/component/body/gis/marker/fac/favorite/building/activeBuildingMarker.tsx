import {useMap} from "react-leaflet";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useGisStore} from "../../../../../../../store/zustand/gis.ts";
import {useEffect} from "react";
import type {BUILDING_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";
import {DataMarker} from "../../../../../../../data/interface/ccomonInterface.ts";
import L from "leaflet";
import ReactDOMServer from "react-dom/server";
import {LEFT_MENU_FAVORITE} from "../../../../../../../data/const/common.ts";
import {useFavoriteStore} from "../../../../../../../store/zustand/favorite.ts";

const ActiveBuildingMarker = () => {
    const map = useMap();

    const {activeBuilding, activeTab, activePatrol, setActiveBuilding} = useLeftStore(useShallow((state) => ({
        activeBuilding: state.activeBuilding,
        activeTab: state.activeTab,
        activePatrol : state.activePatrol,
        setActiveBuilding: state.actions.setActiveBuilding,
    })))

    const {activeZoom, buildingIconShow} = useGisStore(useShallow((state) => ({
        activeZoom: state.activeZoom,
        buildingIconShow: state.buildingIconShow,
    })));

    const {buildFavorites} = useFavoriteStore(useShallow((state) => ({
        buildFavorites: state.buildFavorites,
    })))

    useEffect(() => {

        map.eachLayer((l) => {
            if (l.options.pane === 'activeBuildingMarker') {
                map.removeLayer(l);
            }
        })

        if(buildingIconShow){
            if(activePatrol === null){
                if (activeBuilding) {
                    if (activeTab.cd === LEFT_MENU_FAVORITE.cd) {
                        if (buildFavorites.has(activeBuilding.id)) {
                            const activeBuildingMarker = makeMarker(activeBuilding);
                            map.addLayer(activeBuildingMarker.getLeafletMarker());
                        }
                    }
                }
            }
        }
    }, [activeBuilding, buildFavorites, activeTab, buildingIconShow, activeZoom, activePatrol]);



    const renderIcon = (buildingInfo: BUILDING_TYPE) => {
        return L.divIcon({
            className: "active_building_icon",
            html: ReactDOMServer.renderToString(
                <button className="gis__icon-wrap">
                    <i className={`gis__icon building ${activeBuilding && (activeBuilding.id === buildingInfo.id) ? "active" : ""}`}></i>
                    <div className="name">{buildingInfo.id} <span>{activeBuilding ? activeBuilding.name : '-'}</span>
                    </div>
                </button>
            ),
            iconAnchor: [10, 40], // 중앙 하단에 기준점
            iconSize: [45, 63]
        });
    }

    const makeMarker = (buildingInfo: BUILDING_TYPE) => {
        const marker = new DataMarker(
            [Number(buildingInfo.ycrdnt), Number(buildingInfo.xcrdnt)], {
                pane: 'activeBuildingMarker',
                data: buildingInfo,
                icon: renderIcon(buildingInfo),
                interactive: true,
            });

        marker.on('click', markerClick);
        return marker;
    }

    const markerClick = (e: any) => {
        const markerInfo = e.target.options.data;
        if (activeBuilding) {
            if (activeBuilding.id === markerInfo.id) {
                setActiveBuilding(null);
            } else {
                setActiveBuilding(markerInfo);
            }
        } else {
            setActiveBuilding(markerInfo);
        }
    }


    return null;

}

export default ActiveBuildingMarker;
