import { createRoot } from 'react-dom/client'
import Root from "./component/root.tsx";
import "./assets/common/css/reset.css";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";


const queryClient = new QueryClient();
createRoot(document.getElementById('root')!).render(
    <QueryClientProvider client={queryClient}>
        <Root/>
        <div id="webrtc-overlay-root"></div>
    </QueryClientProvider>
)
