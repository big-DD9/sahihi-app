import { useState, useEffect, useCallback } from 'react'
import apiClient from '../api/client'
import Navbar from '../components/layout/Navbar'
import AdminLoanPanel from '../components/admin/AdminLoanPanel'

export default function AdminDashboardPage() {
  const [loans, setLoans] = useState([])
  const [loading, setLoading] = useState(true)

  const refreshLoans = useCallback(async () => {
    const { data } = await apiClient.get('/loans/pending')
    setLoans(data)
    setLoading(false)
  }, [])

  useEffect(() => {
    refreshLoans()
  }, [refreshLoans])

  if (loading) {
    return (
      <div className="page">
        <Navbar />
        <div className="container" style={{ paddingTop: '2rem' }}>Loading pending loans…</div>
      </div>
    )
  }

  return (
    <div className="page">
      <Navbar />
      <div className="container" style={{ paddingTop: '2rem', paddingBottom: '3rem' }}>
        <AdminLoanPanel loans={loans} onSuccess={refreshLoans} />
      </div>
    </div>
  )
}