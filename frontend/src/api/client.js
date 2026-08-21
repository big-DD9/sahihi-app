import axios from 'axios'

// In dev, this resolves to '/api' and Vite proxies it to localhost:8080.
// In production (Vercel), VITE_API_BASE_URL is set to the real Render
// backend URL, since there's no dev proxy once this is a static build.
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Attach the current JWT (if we have one) to every outgoing request.
// The token is injected from outside via setAuthToken() - see AuthContext.
let currentToken = null

export function setAuthToken(token) {
  currentToken = token
}

apiClient.interceptors.request.use((config) => {
  if (currentToken) {
    config.headers.Authorization = `Bearer ${currentToken}`
  }
  return config
})

// Backend's GlobalExceptionHandler always returns { message, status, timestamp }
// on errors - unwrap that here so components can just read error.message.
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error.response?.data?.message || 'Something went wrong. Please try again.'
    return Promise.reject(new Error(message))
  }
)

export default apiClient