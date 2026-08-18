import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client'
import Navbar from '../components/layout/Navbar'
import Dashboard from '../components/member/Dashboard'
import DepositWithdrawForm from '../components/member/DepositWithdrawForm'
import TransactionHistory from '../components/member/TransactionHistory'

export default function MemberDashboardPage() {
  const [account, setAccount] = useState(null)
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)

  const refreshData = useCallback(async () => {
    const [accountRes, txnRes] = await Promise.all([
      apiClient.get('/accounts/me'),
      apiClient.get('/accounts/transactions'),
    ])
    setAccount(accountRes.data)
    setTransactions(txnRes.data)
    setLoading(false)
  }, [])

  useEffect(() => {
    refreshData()
  }, [refreshData])

  if (loading) {
    return (
      <div className="page">
        <Navbar />
        <div className="container" style={{ paddingTop: '2rem' }}>Loading your account…</div>
      </div>
    )
  }

  return (
    <div className="page">
      <Navbar />
      <div className="container" style={{ paddingTop: '2rem', paddingBottom: '3rem' }}>
        <Dashboard account={account} />

        <div style={{ display: 'flex', gap: '1.5rem', marginTop: '1.5rem', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 320px' }}>
            <DepositWithdrawForm onSuccess={refreshData} />
          </div>
          <div style={{ flex: '1 1 320px' }}>
            <div className="card">
              <div className="card-header">
                <h2>Loans</h2>
              </div>
              <p style={{ color: 'var(--ink-muted)', fontSize: '0.9rem', marginBottom: '1rem' }}>
                Request a loan against your savings, or check the status of an existing one.
              </p>
              <Link to="/dashboard/loans" className="btn btn-primary btn-block">
                Manage loans
              </Link>
            </div>
          </div>
        </div>

        <div style={{ marginTop: '1.5rem' }}>
          <TransactionHistory transactions={transactions} />
        </div>
      </div>
    </div>
  )
}