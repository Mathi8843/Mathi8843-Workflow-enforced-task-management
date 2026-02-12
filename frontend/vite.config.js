import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, ''), // DON'T rewrite if backend already has /api prefix.
        // My backend controllers have @RequestMapping("/api/auth").
        // But TaskController has @RequestMapping("/tasks"), NOT /api/tasks.
        // Wait, TaskController is `/tasks`. NOT `/api/tasks`.
        // UserController is `/users`. NOT `/api/users`.
        // AuthController is `/api/auth`.
        // This is inconsistent backend design.
        // I should fix backend or configure proxy to rewrite carefully.
        // Or better, update backend to use /api prefix for everything.
        // But I shouldn't modify existing API structure unless necessary.
        // So frontend calls to `/api/auth` fit perfectly.
        // Frontend calls to `/tasks` should be proxied too.
        // So I proxy `/api` -> localhost:8080/api (auth)
        // And `/tasks` -> localhost:8080/tasks 
        // And `/users` -> localhost:8080/users
      },
      '/tasks': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
