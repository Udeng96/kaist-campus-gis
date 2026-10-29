import {
    CCTV_TYPE_WHOLE,
    LEFT_CAMPUS_WHOLE,
    LEFT_MENU_CAMPUS,
    LEFT_MENU_FAVORITE
} from "../../../../../../data/const/common.ts";
import {useLeftStore} from "../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useEffect, useState} from "react";
import BuildingArea from "./building/buildingArea.tsx";
import {useQuery} from "@tanstack/react-query";
import {fetchBuildings, fetchCctvs} from "../../../../../../data/api/left/leftApi.ts";
import CctvArea from "./cctv/cctvArea.tsx";

const CampusMenu = () => {
    const {
        activeTab,
        activeBuildingType,
        activeFavoriteArea,
        activeCctvType,
        activeFavoriteCctvType,
        setBuildingList,
        setCctvList
    } = useLeftStore(useShallow((state) => ({
        activeTab: state.activeTab,
        activeBuildingType: state.activeBuildingType,
        activeFavoriteArea : state.activeFavoriteArea,
        activeCctvType : state.activeCctvType,
        activeFavoriteCctvType : state.activeFavoriteCctvType,
        setBuildingList : state.actions.setBuildingList,
        setCctvList : state.actions.setCctvList

    })));
    const [activeArea, setActiveArea] = useState<{cd:string, nm:string}>(LEFT_CAMPUS_WHOLE);
    const [activeType, setActivType] = useState<{id:string, cd:string, nm:string}>(CCTV_TYPE_WHOLE);

    const {data: buildingRes} = useQuery({
        queryKey : ['buildingList'],
        queryFn : () => fetchBuildings(),
        staleTime : Infinity
    })

    const {data : cctvRes} = useQuery({
        queryKey : ['cctvList'],
        queryFn : () => fetchCctvs(),
        staleTime : Infinity
    })

    useEffect(() => {
        if(buildingRes){
            if(buildingRes.data){
                setBuildingList(buildingRes.data);
            }
        }
    }, [buildingRes]);

    useEffect(() => {
        if(cctvRes){
            if(cctvRes.data){
                setCctvList(cctvRes.data.filter((cctv)=> !cctv.streamId.includes("HIGH")));
            }
        }
    }, [cctvRes]);


    useEffect(() => {
        if(activeTab.cd === LEFT_MENU_CAMPUS.cd){
            setActiveArea(activeBuildingType);
        }else if(activeTab.cd === LEFT_MENU_FAVORITE.cd){
            setActiveArea(activeFavoriteArea);
        }

        if(activeTab.cd === LEFT_MENU_CAMPUS.cd){
            setActivType(activeCctvType);
        }else if(activeTab.cd === LEFT_MENU_FAVORITE.cd){
            setActivType(activeFavoriteCctvType);
        }
    }, [activeTab, activeBuildingType, activeFavoriteArea, activeCctvType, activeFavoriteCctvType]);

    return (
        <li className={`tab__item tab__item--campus active`}>
            <BuildingArea activeArea={activeArea}/>
            <CctvArea activeType={activeType} isFavorite={activeTab.cd === LEFT_MENU_FAVORITE.cd}/>
        </li>
    )
}

export default CampusMenu
