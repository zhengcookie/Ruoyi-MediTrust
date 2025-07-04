import { defineConfig } from "vite";

// https://vitejs.dev/config/
export default defineConfig({
  envPrefix: "VITE_", //环境变量默认前缀
  server: {
    // 服务器主机名，默认是 localhost
    host: "localhost",
    // 是否开启自动打开浏览器
    open: true,
    proxy: {
      "^/dev-api": {
        target: `http://localhost:8085/`,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/dev-api/, ""),
      },
    },
  },
});
