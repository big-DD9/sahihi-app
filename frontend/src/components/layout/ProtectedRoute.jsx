import { Navigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

/**
 * Wraps a page and enforces two things:
 *  1. You must be logged in at all - otherwise -> /login
 *  2. If requireRole is set, your role must match - otherwise you're
 *     bounced to your OWN correct dashboard, not a dead end.
 *
 * Usage: <ProtectedRoute requireRole="ADMIN"><AdminDashboardPage /></ProtectedRoute>
 */
export default function ProtectedRoute({ children, requireRole }) {
  const { isAuthenticated, isAdmin, loading } = useAuth()

  if (loading) {
    return null // brief flash while auth state initializes
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  const userRole = isAdmin ? 'ADMIN' : 'MEMBER'
  if (requireRole && userRole !== requireRole) {
    return <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />
  }

  return children
}