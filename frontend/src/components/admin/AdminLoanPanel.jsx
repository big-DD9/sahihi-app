import { useState } from 'react'
import apiClient from '../../api/client'

/**
 * Admin-only: list of PENDING loans with approve/reject actions.
 * Only reachable via /admin, which is itself gated by
 * ProtectedRoute requireRole="ADMIN" - but the backend also enforces
 * this independently via @PreAuthorize, so this UI can never be the
 * only line of defense even if someone bypasses the frontend route.
 */
export default function AdminLoanPanel({ loans, onSuccess }) {
  const formatMoney = (value) =>
    new Intl.NumberFormat('en-KE', { style: 'currency', currency: 'KES' }).format(value)

  return (
    <div className="card">
      <div className="card-header">
        <h2>Pending loan requests</h2>
      </div>

      {loans.length === 0 ? (
        <div className="empty-state">No loans awaiting a decision.</div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {loans.map((loan) => (
            <LoanDecisionRow key={loan.id} loan={loan} formatMoney={formatMoney} onSuccess={onSuccess} />
          ))}
        </div>
      )}
    </div>
  )
}

function LoanDecisionRow({ loan, formatMoney, onSuccess }) {
  const [interestRate, setInterestRate] = useState('10')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function decide(approve) {
    setError('')
    setSubmitting(true)
    try {
      const payload = approve
        ? { approve: true, interestRate: parseFloat(interestRate) }
        : { approve: false }
      await apiClient.put(`/loans/${loan.id}/decision`, payload)
      onSuccess()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ borderBottom: '1px solid var(--border-hairline)', paddingBottom: '1.25rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
        <div className="mono" style={{ fontWeight: 600, fontSize: '1.05rem' }}>
          {formatMoney(loan.amount)}
        </div>
        <span className="stamp stamp-pending">PENDING</span>
      </div>

      <p style={{ fontSize: '0.88rem', color: 'var(--ink-muted)', margin: '0 0 0.75rem' }}>
        {loan.reason}
      </p>

      {error && <div className="form-error-banner">{error}</div>}

      <div style={{ display: 'flex', gap: '0.6rem', alignItems: 'center', flexWrap: 'wrap' }}>
        <label style={{ fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          Interest rate
          <input
            type="number"
            min="0"
            step="0.5"
            value={interestRate}
            onChange={(e) => setInterestRate(e.target.value)}
            style={{
              width: '70px',
              padding: '0.4rem 0.5rem',
              border: '1.5px solid var(--border-hairline)',
              borderRadius: 'var(--radius-sm)',
            }}
          />
          %
        </label>

        <button className="btn btn-primary" onClick={() => decide(true)} disabled={submitting}>
          Approve
        </button>
        <button className="btn btn-danger" onClick={() => decide(false)} disabled={submitting}>
          Reject
        </button>
      </div>
    </div>
  )
}