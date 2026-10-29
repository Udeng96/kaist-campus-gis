import {useLeftStore} from "../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";
import {useEffect, useState} from "react";
import type { EVENT_TYPE } from "../../../../../../data/interface/leftInterface.tsx";

const LastResultLiPage = (props:{events:EVENT_TYPE[]}) => {

    const {lastEventPage, setLastEventPage} = useLeftStore(useShallow((state)=> ({
        lastEventPage : state.lastEventPage,
        setLastEventPage : state.actions.setLastEventPage
    })));
    const [pageList, setPageList] = useState<number[]>([1]);

    useEffect(() => {
        if(props.events!==undefined){
            if(props.events.length === 0) {
                setLastEventPage(1);
            }else{
                let lastPage = Math.floor(props.events.length / 8) ;
                const extra = props.events.length % 8;
                if(extra === 0 ){
                    lastPage = lastPage -1;
                }
                let newPage : number[] = [];
                for(let i = 0; i<=lastPage; i++){
                    newPage.push(i+1);
                }
                setPageList(newPage);
            }
        }else{
            setPageList([1])
        }

    }, [props.events]);


    return(
        <div className="paging">
            <div className="btn-wrap">
                <button type="button" className="btn__paging btn__paging--first" disabled={lastEventPage === 1} onClick={()=>setLastEventPage(1)}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                         viewBox="0 0 24 24" fill="none">
                        <g>
                            <path d="M11 16L7 12L11 8" stroke="currentColor" stroke-width="1.1"
                                  stroke-linecap="round" stroke-linejoin="round"></path>
                            <path d="M15 16.0002L11 12.0002L15 8.00024" stroke="currentColor"
                                  stroke-width="1.1" stroke-linecap="round"
                                  stroke-linejoin="round"></path>
                        </g>
                    </svg>
                </button>
                <button type="button" className="btn__paging btn__paging--prev" disabled={lastEventPage === 1} onClick={()=>setLastEventPage(lastEventPage-1)}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                         viewBox="0 0 24 24" fill="none">
                        <g>
                            <path d="M13 16.0002L9 12.0002L13 8.00024" stroke="currentColor"
                                  stroke-width="1.1" stroke-linecap="round"
                                  stroke-linejoin="round"></path>
                        </g>
                    </svg>
                </button>
            </div>
            <div className="num">
                {
                    pageList.map((page)=>(
                        <a href="#" className={`btn__paging btn__num ${lastEventPage === page ? 'active' : ''}`} onClick={()=>setLastEventPage(page)}>{page}</a>
                    ))
                }
            </div>
            <div className="btn-wrap">
                <button type="button" className="btn__paging btn__paging--next" disabled={lastEventPage === pageList.length} onClick={()=>setLastEventPage(lastEventPage+1)}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                         viewBox="0 0 24 24" fill="none">
                        <path d="M11 8.00024L15 12.0002L11 16.0002" stroke="currentColor"
                              stroke-width="1.1" stroke-linecap="round"
                              stroke-linejoin="round"></path>
                    </svg>
                </button>
                <button type="button" className="btn__paging btn__paging--last" disabled={lastEventPage === pageList.length} onClick={()=>setLastEventPage(pageList.length)}>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                         viewBox="0 0 24 24" fill="none">
                        <path d="M13 8.00024L17 12.0002L13 16.0002" stroke="currentColor"
                              stroke-width="1.1" stroke-linecap="round"
                              stroke-linejoin="round"></path>
                        <path d="M9 8.00012L13 12.0001L9 16.0001" stroke="currentColor"
                              stroke-width="1.1" stroke-linecap="round"
                              stroke-linejoin="round"></path>
                    </svg>
                </button>
            </div>
        </div>
    )
}

export default LastResultLiPage