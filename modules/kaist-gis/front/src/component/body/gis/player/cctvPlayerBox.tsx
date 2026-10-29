import {useLeftStore} from "../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {
    LEFT_MENU_CAMPUS,
    LEFT_MENU_FAVORITE,
    LEFT_MOD_PATROL,
    LEFT_MOD_PATROL_EDIT,
    LEFT_MOD_PATROL_REGI
} from "../../../../data/const/common.ts";
import {useEffect} from "react";
import {useFavoriteStore} from "../../../../store/zustand/favorite.ts";
import CctvPlayer from "./cctvPlayer.tsx";
import {useMainStore} from "../../../../store/zustand/main.ts";
import {useGisStore} from "../../../../store/zustand/gis.ts";

const CctvPlayerBox = () => {

    const {activePatrol,activeTab, selectCctvList, setSelectCctvList, activeBuilding} = useLeftStore(useShallow((state) => ({
        activePatrol : state.activePatrol,
        activeTab: state.activeTab,
        selectCctvList: state.selectCctvList,
        setSelectCctvList : state.actions.setSelectCctvList,
        activeBuilding : state.activeBuilding,
    })));

    const {favorites} = useFavoriteStore(useShallow((state) => ({
        favorites: state.favorites
    })))

    const activeMod = useMainStore((state)=> state.activeMod);
    const playList = useGisStore(state=> state.playList);
    const setPlayList = useGisStore(state => state.actions.setPlayList);
    const removeStreamId = useGisStore(state=>state.removeStreamId);
    const setRemoveStreamId = useGisStore(state=> state.actions.setRemoveStreamId);
    const expandStreamId = useGisStore(state=> state.expandStreamId);
    const setExpandStreamId = useGisStore(state=>state.actions.setExpandStreamId);
    const setActiveFullCctv = useGisStore(state => state.actions.setActiveFullCctv);


    const deleteDragPosition = useGisStore(state => state.actions.deleteDragPosition);

    useEffect(()=>{
        if(activeTab.cd === LEFT_MENU_FAVORITE.cd){
            let newCctvs =selectCctvList.filter((item)=> favorites.has(item.streamId));
            setPlayList(newCctvs);
        }else if(activeTab.cd === LEFT_MENU_CAMPUS.cd){
            setPlayList(selectCctvList);
        }
    },[activeTab, selectCctvList, favorites])

    useEffect(() => {
        if(removeStreamId !== ""){
            setSelectCctvList(selectCctvList.filter((item)=> item.streamId!==removeStreamId))
            setTimeout(()=>{
                deleteDragPosition(removeStreamId);
            },100)
            setRemoveStreamId("");
        }
    }, [removeStreamId]);

    useEffect(() => {
        if(expandStreamId !== ""){
            const cctvData = selectCctvList.find(v => v.streamId === expandStreamId);
            if (cctvData) setActiveFullCctv(cctvData);
            setExpandStreamId("");
        }

    }, [expandStreamId]);



    if (!(activeTab.cd === LEFT_MENU_CAMPUS.cd || activeTab.cd === LEFT_MENU_FAVORITE.cd)) return null;
    if (playList.length === 0) return null;
    if(activeMod.cd === LEFT_MOD_PATROL.cd || activeMod.cd === LEFT_MOD_PATROL_REGI.cd || activeMod.cd === LEFT_MOD_PATROL_EDIT.cd) return null;
    if(activePatrol !== null) return null;
    if(activeBuilding!== null) return null;
    return (
        <>
            {
                playList.map((cctv, idx) => (
                <CctvPlayer key={cctv.streamId} cctv={cctv} idx={idx}/>
            ))}
        </>
    );
};

export default CctvPlayerBox;
