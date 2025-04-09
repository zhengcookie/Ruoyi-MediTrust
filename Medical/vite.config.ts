import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/',
  plugins: [vue()],
  server:{
    port:8081,
    // proxy:{
    //   '/api':{
    //     target:'http://localhost:8085/',
    //     changeOrigin:true,
    //     rewrite: (path) => path.replace(/^\/api/, '')
    //   }
    // }
  }
})
