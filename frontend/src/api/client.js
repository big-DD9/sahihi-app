import axios from 'axios'

// Every component imports THIS instance, never raw axios - that's what
// guarantees the JWT gets attached consistently on every single request.
const apiClient = axios.create({
  baseURL: '/api', // Vite proxies this to http://localhost:8080 in dev
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