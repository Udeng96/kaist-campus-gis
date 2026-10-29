import type {CCTV_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";
import CctvItem from "./cctvItem.tsx";
import {useEffect, useState} from "react";
import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {LEFT_MENU_CAMPUS} from "../../../../../../../data/const/common.ts";

const Cctvs = (props:{activeType:{id:string, cd:string, nm:string},cctvs:CCTV_TYPE[], isFavorite:boolean}) => {
    const {activeBuildingCctvs, activeTab} = useLeftStore(useShallow((state)=> ({
        activeBuildingCctvs : state.activeBuildingCctvs,
        activeTab : state.activeTab
    })))

    const [activeCctvs, setActiveCctvs] = useState<CCTV_TYPE[]>([])

    useEffect(() => {

        if(activeTab.cd === LEFT_MENU_CAMPUS.cd){

            if(activeBuildingCctvs.length > 0){
                const priority = activeBuildingCctvs.map((c) => c.streamId);


                const sortedCctvList = [...props.cctvs].sort((a, b) => {
                    const aIndex = priority.indexOf(a.streamId);
                    const bIndex = priority.indexOf(b.streamId);

                    // priority에 없는 streamId는 indexOf → -1 된다

                    if (aIndex === -1 && bIndex === -1) return 0; // 둘 다 우선순위 없음 → 원래 순서 유지
                    if (aIndex === -1) return 1; // a는 없고 b는 있음 → b가 앞으로
                    if (bIndex === -1) return -1; // b는 없고 a는 있음 → a가 앞으로

                    return aIndex - bIndex; // 둘 다 우선순위에 있다면 priority 순서대로 정렬
                });

                setActiveCctvs(sortedCctvList);
            }else{
                setActiveCctvs([...props.cctvs])
            }
        }else{
            setActiveCctvs([...props.cctvs]);
        }

    }, [props.cctvs, activeBuildingCctvs, activeTab, ]);

    return(
        <div className={`list__body ${props.isFavorite ? 'favorite' : ''}`}>
            <ul className="ct-scroll">
                {
                    activeCctvs.filter((cctv)=> props.activeType.id === '0' || cctv.plcType === props.activeType.id).map((cctv, idx)=>(
                        <CctvItem cctv={cctv} idx={idx} activeType={props.activeType}/>
                    ))
                }
            </ul>
        </div>
    )

}

export default Cctvs