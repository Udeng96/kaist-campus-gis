import {create} from "zustand/react";
import {DEFAULT_LAT_LNG, DEFAULT_ZOOM_LEVEL, TILE_TYPE_SATEL} from "../../data/const/gis.ts";
import type {LatLngTuple} from "leaflet";
import type {CCTV_TYPE, PATROL_TYPE} from "../../data/interface/leftInterface.tsx";

interface GisStoreType {
    activeTile : string,
    activeZoom : number,
    activeCenter: LatLngTuple,
    buildingIconShow : boolean,
    cctvIconShow : boolean,

    isCastMode : boolean,
    castCctvList :CCTV_TYPE[],
    playList : CCTV_TYPE[],

    activeFullCctv : CCTV_TYPE | null,
    activeFullPatrol : PATROL_TYPE | null,

    activeFac : { name: string, type: string, cd: string, xcrdnt: string, ycrdnt: string } | null,
    dragPositions: Record<string,  {  lat: number; lng: number;}>,
    cctvState : string,
    removeStreamId : string,
    expandStreamId : string,

    actions:{
        setActiveTile : (tile:string)=>void,
        setActiveZoom : (zoom:number)=>void,
        setActiveCenter : (center:LatLngTuple)=>void,
        setBuildingIconShow : (show:boolean)=>void,
        setCctvIconShow : (show:boolean)=>void,

        setIsCastMode : (mode:boolean)=>void,
        setCastCctvList : (cctvList:CCTV_TYPE[])=>void,
        setPlayList : (playList:CCTV_TYPE[]) => void,

        setActiveFullCctv : (cctv:CCTV_TYPE | null)=>void,
        setActiveFullPatrol : (patrol:PATROL_TYPE| null)=>void,

        setActiveFac : (fac:{ name: string, type: string, cd: string, xcrdnt: string, ycrdnt: string } | null)=>void,

        setDragPosition: (id: string, pos: {  lat: number; lng: number;}) => void;
        deleteDragPosition : (id:string) => void,

        setCctvState : (state:string)=>void
        setRemoveStreamId : (state:string) => void,
        setExpandStreamId : (state:string) => void,
    }
}

export const useGisStore = create<GisStoreType>((set)=> ({
    activeTile : TILE_TYPE_SATEL,
    activeZoom : DEFAULT_ZOOM_LEVEL,
    activeCenter : DEFAULT_LAT_LNG,
    buildingIconShow : true,
    cctvIconShow : true,


    isCastMode : false,
    castCctvList : [],
    playList : [],

    activeFac : null,

    activeFullCctv : null,
    activeFullPatrol : null,

    dragPositions: {},

    cctvState : 'none',
    removeStreamId : '',
    expandStreamId : '',

    actions :{
        setActiveTile : (tile:string)=>set({activeTile:tile}),
        setActiveZoom : (zoom:number)=>set({activeZoom:zoom}),
        setActiveCenter : (center:LatLngTuple)=>set({activeCenter:center}),
        setBuildingIconShow : (show:boolean)=>set({buildingIconShow:show}),
        setCctvIconShow : (show:boolean)=>set({cctvIconShow:show}),

        setIsCastMode : (mode:boolean)=>set({isCastMode:mode}),
        setCastCctvList : (cctvList:CCTV_TYPE[])=>set({castCctvList:cctvList}),
        setPlayList : (playList:CCTV_TYPE[])=>set({playList:playList}),

        setActiveFac : (fac:{ name: string, type: string, cd: string, xcrdnt: string, ycrdnt: string } | null)=>set({activeFac:fac}),

        setActiveFullCctv : (cctv:CCTV_TYPE | null)=>set({activeFullCctv:cctv}),
        setActiveFullPatrol : (patrol:PATROL_TYPE| null)=>set({activeFullPatrol:patrol}),

        setDragPosition: (id, pos) => set((state) => ({dragPositions: {...state.dragPositions, [id]: pos,}})),


        deleteDragPosition: (id) =>
            set((state) => {
                const next = { ...state.dragPositions };
                delete next[id]; // ✅ key 자체 삭제
                return { dragPositions: next };
            }),

        setCctvState : (state:string)=>set({cctvState:state}),
        setRemoveStreamId : (streamId : string) => set({removeStreamId:streamId}),
        setExpandStreamId : (streamId : string) => set({expandStreamId:streamId}),
    }
}))
