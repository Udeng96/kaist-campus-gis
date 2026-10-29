import TabHead from "./tab/tabHead.tsx";
import TabBody from "./tab/tabBody.tsx";
import PatrolHead from "./patrol/patrolHead.tsx";
import PatrolBody from "./patrol/patrolBody.tsx";
import {useMainStore} from "../../../store/zustand/main.ts";
import {useShallow} from "zustand/react/shallow";
import {LEFT_MOD_PATROL, LEFT_MOD_PATROL_EDIT, LEFT_MOD_PATROL_REGI, LEFT_MOD_TAB} from "../../../data/const/common.ts";
import RegiHead from "./patrol/mod/regi/regiHead.tsx";
import RegiBody from "./patrol/mod/regi/regiBody.tsx";
import RegiFoot from "./patrol/mod/regi/regiFoot.tsx";
import {useQuery} from "@tanstack/react-query";
import {fetchPatrols} from "../../../data/api/left/leftApi.ts";
import {useEffect} from "react";
import {useLeftStore} from "../../../store/zustand/left.ts";
import {useFavoriteStore} from "../../../store/zustand/favorite.ts";
import {fetchSensros} from "../../../data/api/common/commonApi.ts";

const Left = () => {

    const {activeLeft, activeMod, setActiveLeft, setSensors} = useMainStore(useShallow((state) => ({
        activeLeft: state.activeLeft,
        activeMod: state.activeMod,
        setActiveLeft: state.actions.setActiveLeft,
        setSensors : state.actions.setSensors,
    })))

    const {cctvList, buildingList, setPatrolList} = useLeftStore(useShallow((state)=> ({
        cctvList : state.cctvList,
        buildingList : state.buildingList,
        setPatrolList : state.actions.setPatrolList
    })))

    const {initFavorites, syncFavorites, setFavorites, setBuildFavorites} = useFavoriteStore(useShallow((state)=> ({
        initFavorites : state.actions.initFavorites,
        syncFavorites : state.actions.syncFavorites,
        setFavorites : state.actions.setFavorites,
        setBuildFavorites : state.actions.setBuildFavorites
    })))


    useEffect(() => {
        (async () => {
            await initFavorites();
        })();
    }, []);

    useEffect(() => {

        const timer = setInterval(() => {
            (async () => {
                await syncFavorites();
            })();
        }, 25000);

        return () => clearInterval(timer);
    }, []);

    useEffect(() => {

        if(cctvList.length > 0){
            const filterCctvs = cctvList.filter((item)=> item.favoriteYn === '1');
            const filterCctvIds : string[] = filterCctvs.map((item)=> item.streamId);
            setFavorites(filterCctvIds);
        }else{
            setFavorites([]);
        }

    }, [cctvList]);

    useEffect(() => {
        if(buildingList.length > 0){
            const filterBuildings = buildingList.filter((item)=> item.favoriteYn === '1');
            const filterBuildingIds : string[] = filterBuildings.map((item)=> item.id);
            setBuildFavorites(filterBuildingIds);
        }else{
            setBuildFavorites([]);
        }
    }, [buildingList]);

    useEffect(() => {
        const handleBeforeUnload = () => syncFavorites();
        window.addEventListener("beforeunload", handleBeforeUnload);
        return () => window.removeEventListener("beforeunload", handleBeforeUnload);
    }, []);

    const {data : sensors} = useQuery({
        queryKey : ['allSensors'],
        queryFn : () => fetchSensros(),
        staleTime : Infinity,
    })

    useEffect(() => {
        if(sensors && sensors.data){
            setSensors(sensors.data);
        }
    },[sensors])


    const {data: patrolsRes} = useQuery({
        queryKey: ['patrols'],
        queryFn: () => fetchPatrols(),
        staleTime: Infinity,
    })

    useEffect(() => {
        if(patrolsRes){
            if(patrolsRes.data){
                setPatrolList(patrolsRes.data);
            }
        }
    }, [patrolsRes]);


    return (
        <section className={`content content--left ${activeLeft ? 'active' : ''}`}>
            <div className="dimmed"></div>
            <button type="button" className="btn-slide btn-slide--left" onClick={() => setActiveLeft(!activeLeft)}/>
            <div className={`tab ${activeMod.cd === LEFT_MOD_TAB.cd ? 'active' : ''}`}>
                <TabHead/>
                <TabBody/>
            </div>
            {/*순찰 모드*/}
            <div className={`patrol ${activeMod.cd === LEFT_MOD_PATROL.cd ? 'active' : ''}`}>
                <PatrolHead/>
                <PatrolBody/>
            </div>
            {/*순찰 등록 모드*/}
            <div className={`patrol-regi`}>
                <div
                    className={`content__frame content__frame--register ${activeMod.cd === LEFT_MOD_PATROL_REGI.cd || activeMod.cd === LEFT_MOD_PATROL_EDIT.cd ? 'active' : ''}`}>
                    <RegiHead/>
                    <RegiBody/>
                    <RegiFoot/>
                </div>
            </div>


        </section>
    )
}

export default Left