import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vuetify from 'vite-plugin-vuetify';

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const backendUrl = env.VITE_API_PROXY_TARGET || 'http://localhost:8080';

  return {
    plugins: [
      vue(),
      vuetify({ autoImport: true }),
    ],
    server: {
      port: 5173,
      proxy: {
        // Forward /api calls to the Spring Boot backend during dev
        '/api': {
          target: backendUrl,
          changeOrigin: true,
          // Strip /api prefix when forwarding, since Spring Boot routes
          // are mounted at /api/** already
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
    build: {
      sourcemap: true,
      chunkSizeWarningLimit: 800,
    },
  };
});