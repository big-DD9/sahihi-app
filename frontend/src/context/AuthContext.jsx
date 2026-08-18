import { createContext, useContext, useState, useCallback } from 'react'
import apiClient, { setAuthToken } from '../api/client'

const AuthContext = createContext(null)

/**
 * Single source of truth for "who is logged in right now".
 * Held in React state only (not localStorage) - kept simple and safe
 * for this project; a production app might add refresh-token persistence.
 *
 * Wrap the whole app in <AuthProvider> once, then any component can call
 * useAuth() to read the user, or call login()/register()/logout().
 */
function AuthProvider({ children }) {
  const [user, setUser] = useState(null) // { email, fullName, role }
  const [loading, setLoading] = useState(true)

  // On first load, there's no persisted session - user starts logged out.
  useState(() => setLoading(false))

  const login = useCallback(async (email, password) => {
    const { data } = await apiClient.post('/auth/login', { email, password })
    setAuthToken(data.token)
    setUser({ email: data.email, fullName: data.fullName, role: data.role })
    return data
  }, [])

  const register = useCallback(async (fullName, email, password) => {
    const { data } = await apiClient.post('/auth/register', { fullName, email, password })
    setAuthToken(data.token)
    setUser({ email: data.email, fullName: data.fullName, role: data.role })
    return data
  }, [])

  const logout = useCallback(() => {
    setAuthToken(null)
    setUser(null)
  }, [])

  const value = {
    user,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
    loading,
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

export { AuthProvider, useAuth }