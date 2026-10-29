import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import * as path from "path";

export default defineConfig(({mode}) => {


  return {

    plugins: [react()],
    build: {
      chunkSizeWarningLimit: 2600,
      sourcemap: false
    },
    // vite config
    define: {
      isDev : mode === "development",
      global: 'globalThis',
    },
    resolve : {
      alias:{
        "@" : path.resolve(__dirname, "src")
      }
    },
    base: "/kaist/gis/"
  }
})
