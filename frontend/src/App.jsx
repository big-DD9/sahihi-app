import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import ProtectedRoute from './components/layout/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import MemberDashboardPage from './pages/MemberDashboardPage'
import LoansPage from './pages/LoansPage'
import AdminDashboardPage from './pages/AdminDashboardPage'

function AppRoutes() {
  const { isAuthenticated, isAdmin } = useAuth()

  return (
    <Routes>
      <Route
        path="/login"
        element={
          isAuthenticated ? <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace /> : <LoginPage />
        }
      />
      <Route
        path="/register"
        element={
          isAuthenticated ? <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace /> : <RegisterPage />
        }
      />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute requireRole="MEMBER">
            <MemberDashboardPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/dashboard/loans"
        element={
          <ProtectedRoute requireRole="MEMBER">
            <LoansPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute requireRole="ADMIN">
            <AdminDashboardPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/"
        element={<Navigate to={isAuthenticated ? (isAdmin ? '/admin' : '/dashboard') : '/login'} replace />}
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  )
}