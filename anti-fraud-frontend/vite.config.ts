import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  server: {
    port: 5000,
    host: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
      }
    }
  },
  build: {
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'echarts': ['echarts', 'vue-echarts'],
          'vendor': ['vue', 'vue-router', 'pinia'],
          'axios': ['axios']
        },
        sourcemap: false,
        // CDN 配置
        assetFileNames: 'assets/[hash].[ext]',
        chunkFileNames: 'chunks/[hash].[ext]',
        entryFileNames: 'entry/[hash].js'
      }
    },
    outDir: 'dist',
    assetsDir: 'assets',
    assetsInlineLimit: 4096,
    // 图片优化
    target: 'es2015',
    cssTarget: 'chrome61',
    // 启用图片压缩
    minifyAssets: true
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus', 'axios', 'echarts']
  },
  // 静态资源CDN配置
  base: process.env.NODE_ENV === 'production' ? 'https://cdn.example.com' : '/'
})