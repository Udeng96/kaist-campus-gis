import RightTab from "./body/campus/tab/rightTab.tsx";
import CctvRegi from "./body/campus/regi/cctv/cctvRegi.tsx";
import {useShallow} from "zustand/react/shallow";
import {useMainStore} from "../../../store/zustand/main.ts";
import {
    LEFT_MENU_CAMPUS,
    LEFT_MENU_EVENT, LEFT_MENU_FAVORITE, LEFT_MOD_PATROL, LEFT_MOD_PATROL_EDIT, LEFT_MOD_PATROL_REGI,
    RIGHT_MOD_EVENT_CCTV,
    RIGHT_MOD_NONE,
    RIGHT_MOD_TAB
} from "../../../data/const/common.ts";
import {useEffect, useState} from "react";
import {useLeftStore} from "../../../store/zustand/left.ts";
import EventCctv from "./body/event/cctv/eventCctv.tsx";
import Passive from "./body/event/passive/passive.tsx";

const Right = () => {

    const {
        activeMod,
        activeRightMod,
        activeRightEventMod,
        setActiveRightMod,
        setActiveRightEventMod
    } = useMainStore(useShallow((state) => ({
        activeMod: state.activeMod,
        activeRightMod: state.activeRightMod,
        activeRightEventMod: state.activeRightEventMod,
        setActiveRightMod: state.actions.setActiveRightMod,
        setActiveRightEventMod: state.actions.setActiveRightEventMod
    })))

    const {activeTab, activeBuilding, activeEvent} = useLeftStore(useShallow((state) => ({
        activeTab: state.activeTab,
        activeBuilding: state.activeBuilding,
        activeEvent: state.activeEvent,
    })));
    const [rightOpen, setRightOpen] = useState<boolean>(false);


    useEffect(() => {
        if (activeBuilding) {
            setActiveRightMod(RIGHT_MOD_TAB);
        } else {
            setActiveRightMod(RIGHT_MOD_NONE);
        }
    }, [activeBuilding]);

    useEffect(() => {
        if (activeEvent) {
            setActiveRightEventMod(RIGHT_MOD_EVENT_CCTV);
        } else {
            setActiveRightEventMod(RIGHT_MOD_NONE);
        }
    }, [activeEvent]);

    useEffect(() => {
        if(activeMod.cd === LEFT_MOD_PATROL.cd || activeMod.cd === LEFT_MOD_PATROL_REGI.cd || activeMod.cd === LEFT_MOD_PATROL_EDIT.cd) {
            setRightOpen(false);
        }else{
            if (activeTab.cd === LEFT_MENU_CAMPUS.cd || activeTab.cd === LEFT_MENU_FAVORITE.cd) {
                if (activeRightMod.cd === RIGHT_MOD_NONE.cd) {
                    setRightOpen(false);
                } else {
                    setRightOpen(true);
                }
            } else if (activeTab.cd === LEFT_MENU_EVENT.cd) {
                if (activeRightEventMod.cd === RIGHT_MOD_NONE.cd) {
                    setRightOpen(false);
                } else {
                    setRightOpen(true);
                }
            } else {
                setRightOpen(false);
            }
        }

    }, [activeTab, activeMod, activeRightMod, activeRightEventMod]);

    return (
        <section className={`content content--right ${rightOpen ? 'active' : ''}`}>
            <div className="dimmed"></div>
            <RightTab/>
            <CctvRegi/>
            <EventCctv/>
            <Passive/>
        </section>
    )
}

export default Right