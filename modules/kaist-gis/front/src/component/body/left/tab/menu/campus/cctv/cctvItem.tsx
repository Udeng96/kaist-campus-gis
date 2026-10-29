import {useLeftStore} from "../../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useEffect, useState} from "react";
import type {CCTV_TYPE} from "../../../../../../../data/interface/leftInterface.tsx";
import * as React from "react";
import {RIGHT_MOD_CCTV_EDIT, RIGHT_MOD_CCTV_REGI, TOAST_TYPE} from "../../../../../../../data/const/common.ts";
import {useFavoriteStore} from "../../../../../../../store/zustand/favorite.ts";
import {useMainStore} from "../../../../../../../store/zustand/main.ts";

const CctvItem = (props: { cctv: CCTV_TYPE, idx: number, activeType: { id: string, cd: string, nm: string } }) => {

    const {
        activeTab,
        selectCctvList,
        activeBuildingCctvs,
        activeBuilding,
        setSelectCctvList,
        editCctv,
        setEditCctv,
        setActiveBuilding,
        setActiveBuildingCctvs
    } = useLeftStore(useShallow((state) => ({
        activeTab : state.activeTab,
        selectCctvList: state.selectCctvList,
        activeBuildingCctvs: state.activeBuildingCctvs,
        activeBuilding: state.activeBuilding,
        setSelectCctvList: state.actions.setSelectCctvList,
        editCctv: state.editCctv,
        setEditCctv: state.actions.setEditCctv,
        setActiveBuilding : state.actions.setActiveBuilding,
        setActiveBuildingCctvs : state.actions.setActiveBuildingCctvs,
    })))

    const {favorites, toggleFavorite} = useFavoriteStore(useShallow((state) => ({
        favorites: state.favorites,
        toggleFavorite: state.actions.toggleFavorite
    })));

    const {setActiveToast, setActiveRightMod, activeRightMod} = useMainStore(useShallow((state) => ({
        setActiveToast: state.actions.setActiveToast,
        setActiveRightMod : state.actions.setActiveRightMod,
        activeRightMod : state.activeRightMod,
    })))

    const [classNm, setClassNm] = useState<string>("");

    useEffect(() => {

            const activeBuildingCctvIds = activeBuildingCctvs.map((cctv) => cctv.streamId);
            if (activeBuildingCctvIds.includes(props.cctv.streamId)) {
                setClassNm("highlight");
            } else {
                setClassNm("");
            }


        }, [activeBuildingCctvs, props.cctv]);

    useEffect(() => {
        if(activeBuilding === null){
            const selectIds = selectCctvList.map((cctv) => cctv.streamId);
            if(classNm !== "highlight"){
                if(selectIds.includes(props.cctv.streamId)){
                    setClassNm("active");
                }else{
                    setClassNm("");
                }
            }
        }

    }, [selectCctvList, activeTab, activeBuilding]);

    const handleItem = () => {
        if(activeRightMod.cd !== RIGHT_MOD_CCTV_REGI.cd && activeRightMod.cd !== RIGHT_MOD_CCTV_EDIT.cd){
            if (activeBuilding=== null) {
                if (selectCctvList.includes(props.cctv)) {
                    const newSelectCctvList = selectCctvList.filter((cctv) => cctv.streamId !== props.cctv.streamId);
                    setSelectCctvList(newSelectCctvList);
                } else {
                    setSelectCctvList([...selectCctvList, props.cctv]);
                }
            }
        }else{
            setActiveToast({cd:TOAST_TYPE.WARNING, msg : '편집 모드를 종료한 후 다시 시도해주세요.'})
        }
    }

    const handleEditBtn = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
        e.stopPropagation();
        if(activeRightMod.cd === RIGHT_MOD_CCTV_REGI.cd){
            setActiveToast({cd:TOAST_TYPE.WARNING, msg : '편집 모드를 종료한 후 다시 시도해주세요.'})
        }else{
            setSelectCctvList([]);
            if(activeBuilding){
                setActiveBuilding(null);
                setActiveBuildingCctvs([]);
                setTimeout(()=>{
                    setEditCctv(props.cctv);
                    setActiveRightMod(RIGHT_MOD_CCTV_EDIT);
                },1000)
            }else{
                setEditCctv(props.cctv);
                setActiveRightMod(RIGHT_MOD_CCTV_EDIT);

            }
        }
    }


    const handleFavoriteBtn = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>, cctv: CCTV_TYPE) => {
        e.stopPropagation();
        if(activeRightMod.cd !== RIGHT_MOD_CCTV_REGI.cd && activeRightMod.cd !== RIGHT_MOD_CCTV_EDIT.cd){
            if (favorites.has(cctv.streamId)) {
                setActiveToast({cd: TOAST_TYPE.SUCCESS, msg: '즐겨찾기가 해제되었습니다.'})
            } else {
                setActiveToast({cd: TOAST_TYPE.SUCCESS, msg: '즐겨찾기가 등록되었습니다.'})
            }
            toggleFavorite(props.cctv.streamId)
        }else{
            setActiveToast({cd:TOAST_TYPE.WARNING, msg : '편집 모드를 종료한 후 다시 시도해주세요.'})
        }

    }

    const setPlcType = (plcType: string) => {
        if (plcType === '1') {
            return 'in'
        } else if (plcType === '2') {
            return 'out'
        } else if (plcType === '3') {
            return 'flame'
        }
    }

    const setPlcTypeNm = (plcType: string) => {
        if (plcType === '1') {
            return '내부'
        } else if (plcType === '2') {
            return '외부'
        } else if (plcType === '3') {
            return '불꽃'
        }
    }

    const setIdx = () => {
        return selectCctvList.findIndex((cctv) => cctv.streamId === props.cctv.streamId);
    }

    return (
        <li className={`list__item ${setPlcType(props.cctv.plcType)} ${classNm}`}
            onClick={() => handleItem()}>
            <div><i className="ic-mark">{classNm === 'highlight' ? props.idx + 1 : classNm === 'active' ? setIdx()+1 :  ''}</i>{setPlcTypeNm(props.cctv.plcType)}</div>
            <div>{props.cctv.cctvNm}</div>
            <div className="frame">
                <button type="button"
                        className={`btc-edit ${(editCctv && (editCctv.streamId === props.cctv.streamId)) ? "active" : ""}`}
                        disabled={activeRightMod.cd === RIGHT_MOD_CCTV_REGI.cd}
                        onClick={(e) => handleEditBtn(e)}
                >
                </button>
                <button type="button"
                        onClick={(e) => handleFavoriteBtn(e, props.cctv)}
                        className={`btn-favorites ${favorites.has(props.cctv.streamId) ? "active" : ""} `}></button>
            </div>
        </li>
    )
}

export default CctvItem