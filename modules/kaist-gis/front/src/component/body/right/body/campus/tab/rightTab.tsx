import RightTabHead from "./rightTabHead.tsx";
import RightTabBody from "./rightTabBody.tsx";
import {useMainStore} from "../../../../../../store/zustand/main.ts";
import {LEFT_MENU_CAMPUS, LEFT_MENU_FAVORITE, RIGHT_MOD_TAB} from "../../../../../../data/const/common.ts";
import {useLeftStore} from "../../../../../../store/zustand/left.ts";
import {useShallow} from "zustand/react/shallow";

const RightTab = () => {
    const activeRightMod = useMainStore((state)=> state.activeRightMod);
    const {activeTab} = useLeftStore(useShallow((state)=> ({
        activeTab : state.activeTab,
    })));



    return (
        <div className={`content__frame content__frame--details facility ${(activeTab.cd === LEFT_MENU_CAMPUS.cd || activeTab.cd === LEFT_MENU_FAVORITE.cd) && activeRightMod.cd === RIGHT_MOD_TAB.cd ? 'active' : ''}`}>
            <div className="tab ">
                <RightTabHead/>
                <RightTabBody/>
            </div>
        </div>
    )
}

export default RightTab