import {useEffect, useRef} from "react";
import {useGisStore} from "../../../../store/zustand/gis";
import type {CCTV_TYPE} from "../../../../data/interface/leftInterface";
import L, {type LatLngExpression} from "leaflet";
import {Marker, Polyline, useMap} from "react-leaflet";
import {CCTV_TYPE_FLAME, CCTV_TYPE_IN, CCTV_TYPE_OUT} from "../../../../data/const/common";
import * as React from "react";

const CctvPlayer =  React.memo(({ cctv, idx }: { cctv: CCTV_TYPE, idx:number,  })  => {
    const map = useMap();
    const markerRef = useRef(null);
    const dragPos = useGisStore(state => state.dragPositions[cctv.streamId]);
    const setDrag = useGisStore(state => state.actions.setDragPosition);
    const setRemoveStreamId = useGisStore(state=> state.actions.setRemoveStreamId);



    //@ts-ignore
    const activeZoom = useGisStore(state => state.activeZoom);
    const setExpandStreamId = useGisStore(state=>state.actions.setExpandStreamId);


    const pos: LatLngExpression = dragPos
        ? [dragPos.lat, dragPos.lng]
        : [Number(cctv.ycrdnt), Number(cctv.xcrdnt)];

    const setCctvType = (plcType: string) => {
        if (plcType === CCTV_TYPE_IN.id) return CCTV_TYPE_IN.cd;
        if (plcType === CCTV_TYPE_OUT.id) return CCTV_TYPE_OUT.cd;
        return CCTV_TYPE_FLAME.cd;
    };

    useEffect(() => {
        const root = document.getElementById("root");
        if (!root) return;

        const onClick = (e: any) => {
            const closeBtn = e.target.closest(".btn-close");
            if (closeBtn) {
                const id = closeBtn.dataset.streamId;
                setRemoveStreamId(id);
                return;
            }

            const expandBtn = e.target.closest(".btn-expand");
            if (expandBtn) {
                const id = expandBtn.dataset.streamId;
                setExpandStreamId(id);

            }
        };

        root.addEventListener("click", onClick);
        return () => root.removeEventListener("click", onClick);
    }, []);






    const createIcon = () =>
        L.divIcon({
            html: `
            <div class="cctv__layer cctv__layer--${setCctvType(cctv.plcType)}">
                <div class="cctv__layer__head">
                    <div class="frame">
                        <div class="num">${idx+1}</div>
                        <p>${cctv.cctvNm}</p>
                    </div>
                    <button 
                        class="btn-close" 
                        data-stream-id="${cctv.streamId}">
                    </button>
                </div>

                <div class="cctv__layer__body">
                    <div class="cctv__in" id="cctv-player-${cctv.streamId}">
                        
                    </div>
                    <button class="btn-expand" data-stream-id="${cctv.streamId}"></button>
                </div>
            </div>
        `,
            className: "",
            pane: "cctvPlayer",
            iconSize: [130, 100],
            iconAnchor: [40, 40],
        });


    useEffect(() => {
        const updateZoom = () => {
            useGisStore.setState({activeZoom: map.getZoom()});
        };

        map.on("zoom", updateZoom);
        map.on("move", updateZoom);

        return () => {
            map.off("zoom", updateZoom);
            map.off("move", updateZoom);
        };
    }, [map]);

    const getPlayerCenterLatLng = () => {
        const base = L.latLng(
            dragPos ? dragPos.lat : Number(cctv.ycrdnt),
            dragPos ? dragPos.lng : Number(cctv.xcrdnt)
        );

        const iconWidth = 130;
        const iconHeight = 100;
        const anchorX = 65;
        const anchorY = 50;

        let offsetX = (iconWidth / 2) - anchorX + 50;
        let offsetY = (iconHeight / 2) - anchorY + 20;

        const basePoint = map.latLngToLayerPoint(base);
        return map.layerPointToLatLng(basePoint.add([offsetX, offsetY]));
    };

    const adjustedEnd = getPlayerCenterLatLng();

    const getColor = (plcType: string) => {
        if (plcType === '2') return '#76D066';
        if (plcType === '1') return '#3B82F6';
        return '#907AFF';
    };

    return (
        <>
            <Marker
                ref={markerRef}
                icon={createIcon()}
                position={pos}
                draggable
                pane="cctvPlayer"
                eventHandlers={{
                    dragstart: (e) => {
                        const icon = e.target._icon as HTMLElement;
                        icon.style.zIndex = "99999";   // 🔥 최상단으로 올리기
                        icon.style.position = "relative";
                        icon.style.transform = "scale(1.05)";  // 살짝 확대
                    },
                    dragend: (e) => {
                        const { lat, lng } = e.target.getLatLng();
                        setDrag(cctv.streamId, { lat, lng });

                        const icon = e.target._icon as HTMLElement;
                        icon.style.zIndex = "";        // 복원
                        icon.style.transform = "";
                        icon.style.position = "";
                    }
                }}
            />

            {dragPos && (
                <>
                    <Polyline
                        pane="polylineBorderPane"
                        positions={[
                            [Number(cctv.ycrdnt), Number(cctv.xcrdnt)],
                            adjustedEnd
                        ]}
                        pathOptions={{color: "#ffffff", weight: 4, opacity: 0.85}}
                    />
`
                    <Polyline
                        pane="polylinePane"
                        positions={[
                            [Number(cctv.ycrdnt), Number(cctv.xcrdnt)],
                            adjustedEnd
                        ]}
                        pathOptions={{color: getColor(cctv.plcType), weight: 6}}
                    />
                </>
            )}
        </>
    );
});

export default CctvPlayer;
