import {create} from "zustand/react";
import {LEFT_MOD_TAB, MODAL_NONE, RIGHT_MOD_NONE} from "../../data/const/common.ts";
import type {EVENT_TYPE} from "../../data/interface/leftInterface.tsx";
import type {SensorResponse, WeatherType} from "../../data/interface/ccomonInterface.ts";

interface MainStoreType {
    sensors : SensorResponse | null,
    weather : WeatherType | null,
    activeModal : {cd:string, nm:string},
    activeToast : {cd:string, msg:string},
    activeWsEvent : EVENT_TYPE | null,
    activeMod : {cd:string, nm:string},
    activeRightMod : {cd:string, nm:string},
    activeRightEventMod : {cd:string, nm:string},
    activeLeft : boolean,
    webSocketEvents : EVENT_TYPE[],
    actions:{
        setSensors : (sensors : SensorResponse | null ) =>  void;
        setWeather : (weather : WeatherType | null ) => void;
        setActiveModal : (modal:{cd:string, nm:string})=>void;
        setActiveToast : (toast:{cd:string, msg:string})=> void;
        setActiveWsEvent : (event:EVENT_TYPE | null )=>void;
        setActiveMod : (mod:{cd:string, nm:string})=>void;
        setActiveRightMod : (mod:{cd:string, nm:string})=>void;
        setActiveRightEventMod : (mod:{cd:string, nm:string})=>void;
        setActiveLeft : (activeLeft:boolean)=>void;
        setWebSocketEvents : (webSocketEvents: EVENT_TYPE[]) => void;
    }
}

export const useMainStore = create<MainStoreType>((set)=> ({
    sensors : null,
    weather : null,
    activeModal : MODAL_NONE,
    activeToast : {cd:"NONE", msg:""},
    webSocketEvents : [],
    activeWsEvent : null,
    activeMod : LEFT_MOD_TAB,
    activeRightMod : RIGHT_MOD_NONE,
    activeRightEventMod : RIGHT_MOD_NONE,
    activeLeft : true,
    actions:{
        setSensors : (sensors : SensorResponse | null) => set({sensors : sensors}),
        setWeather : (weather : WeatherType | null) => set({weather : weather}),
        setActiveModal : (modal:{cd:string, nm:string})=>set({activeModal:modal}),
        setActiveToast : (toast:{cd:string, msg:string})=>set({activeToast:toast}),
        setWebSocketEvents : (webSocketEvents : EVENT_TYPE[]) => set({webSocketEvents : webSocketEvents}),
        setActiveWsEvent : (event:EVENT_TYPE|null)=>set({activeWsEvent:event}),
        setActiveMod : (mod:{cd:string, nm:string})=>set({activeMod:mod}),
        setActiveRightMod : (mod:{cd:string, nm:string})=>set({activeRightMod:mod}),
        setActiveRightEventMod : (mod:{cd:string, nm:string})=>set({activeRightEventMod:mod}),
        setActiveLeft : (activeLeft:boolean)=>set({activeLeft:activeLeft}),
    }
}))

