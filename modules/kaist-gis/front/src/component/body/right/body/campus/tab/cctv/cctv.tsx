import CctvLiCnt from "../../../common/cctvLiCnt.tsx";
import {useRightStore} from "../../../../../../../store/zustand/right.ts";
import {useShallow} from "zustand/react/shallow";
import {
    CCTV_TYPE_FLAME,
    CCTV_TYPE_IN, CCTV_TYPE_OUT,
    CCTV_TYPE_WHOLE,
    RIGHT_MENU_CCTV
} from "../../../../../../../data/const/common.ts";
import CctvLiPage from "../../../common/cctvLiPage.tsx";
import CctvLi from "../../../common/cctvLi.tsx";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useEffect, useState} from "react";
import type {CCTV_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";

const Cctv = () => {

    const {activeRightMenu, activeCctvType, setActiveCctvType} = useRightStore(useShallow((state) => ({
        activeRightMenu: state.activeRightMenu,
        activeCctvType: state.activeCctvType,
        setActiveCctvType : state.actions.setActiveCctvType,
    })))
    const [cctvs, setCctvs] = useState<CCTV_TYPE[]>([]);
    const activeBuildingCctvs = useLeftStore((state) => (state.activeBuildingCctvs));


    useEffect(() => {
        if(activeBuildingCctvs.length > 0){
            if(activeCctvType.cd === CCTV_TYPE_WHOLE.cd){
                setCctvs([...activeBuildingCctvs]);
            }else{
                setCctvs(activeBuildingCctvs.filter((cctv)=> setFilterType(cctv.plcType)));
            }
        }else{
            setActiveCctvType(CCTV_TYPE_WHOLE);
            setCctvs([]);
        }
    }, [activeCctvType, activeBuildingCctvs]);

    const setFilterType = (plcType : string) => {
        if(activeCctvType.cd === CCTV_TYPE_IN.cd){
            return plcType === '1';
        }else if(activeCctvType.cd === CCTV_TYPE_OUT.cd){
            return plcType === '2';
        }else if(activeCctvType.cd === CCTV_TYPE_FLAME.cd){
            return plcType === '3';
        }else{
            return true;
        }
    }



    return (
        <li className={`tab__item tab__item--cctv ${activeRightMenu.cd === RIGHT_MENU_CCTV.cd ? 'active' : ''}`}>
            <div className="content__sub-head">
                <h2 className="content__sub-title">실시간 CCTV<span>{activeBuildingCctvs.length}</span>대</h2>
            </div>
            <CctvLiCnt cctvs={activeBuildingCctvs}/>
            <CctvLi cctvs={[...cctvs]}/>
            <CctvLiPage cctvs={[...cctvs]}/>
        </li>
    )

}

export default Cctv