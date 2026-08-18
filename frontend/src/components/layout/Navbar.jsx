import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

/**
 * Shared navbar for both portals. The admin-portal class switches the
 * navbar to a near-black/brass variant (see index.css) so the two
 * portals are visually distinct at a glance - you always know which
 * one you're in.
 */
export default function Navbar() {
  const { user, isAdmin, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <nav className={`navbar ${isAdmin ? 'admin-portal' : ''}`}>
      <span className="navbar-brand">
        Sahihi
        <span className="portal-tag">{isAdmin ? 'Admin' : 'Member'}</span>
      </span>

      {user && (
        <div className="navbar-user">
          <span>{user.fullName}</span>
          <button onClick={handleLogout}>Log out</button>
        </div>
      )}
    </nav>
  )
}