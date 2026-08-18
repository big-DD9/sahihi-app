import { useState, useEffect, useCallback } from 'react'
import apiClient from '../api/client'
import Navbar from '../components/layout/Navbar'
import LoanRequestForm from '../components/member/LoanRequestForm'
import MyLoans from '../components/member/MyLoans'

export default function LoansPage() {
  const [loans, setLoans] = useState([])
  const [loading, setLoading] = useState(true)

  const refreshLoans = useCallback(async () => {
    const { data } = await apiClient.get('/loans/me')
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
        <div className="container" style={{ paddingTop: '2rem' }}>Loading your loans…</div>
      </div>
    )
  }

  return (
    <div className="page">
      <Navbar />
      <div className="container" style={{ paddingTop: '2rem', paddingBottom: '3rem' }}>
        <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 320px' }}>
            <LoanRequestForm onSuccess={refreshLoans} />
          </div>
          <div style={{ flex: '1 1 380px' }}>
            <MyLoans loans={loans} onSuccess={refreshLoans} />
          </div>
        </div>
      </div>
    </div>
  )
}