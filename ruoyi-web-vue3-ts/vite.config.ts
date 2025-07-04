import { fileURLToPath, URL } from "node:url";
import createVitePlugins from "./vite/plugins";
import serveConfig from "./vite/vite.config.serve";
import buildConfig from "./vite/vite.config.serve";
import { defineConfig, loadEnv } from "vite";

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
  const env = loadEnv(mode, process.cwd());
  const { VITE_APP_ENV } = env;
  const config = command === "build" ? buildConfig : serveConfig;
  return {
    plugins: createVitePlugins(VITE_APP_ENV, command === "build"),
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
    ...config,
  };
});
