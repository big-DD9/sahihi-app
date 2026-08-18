import { useState } from 'react'
import apiClient from '../../api/client'

const STAMP_CLASSES = {
  PENDING: 'stamp-pending',
  APPROVED: 'stamp-approved',
  REJECTED: 'stamp-rejected',
  REPAID: 'stamp-repaid',
}

/**
 * Shows a member's own loan history with a stamp badge per status,
 * and a quick repayment form for any APPROVED (outstanding) loan.
 */
export default function MyLoans({ loans, onSuccess }) {
  const formatMoney = (value) =>
    new Intl.NumberFormat('en-KE', { style: 'currency', currency: 'KES' }).format(value)

  return (
    <div className="card">
      <div className="card-header">
        <h2>My loans</h2>
      </div>

      {loans.length === 0 ? (
        <div className="empty-state">You haven't requested a loan yet.</div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {loans.map((loan) => (
            <LoanRow key={loan.id} loan={loan} formatMoney={formatMoney} onSuccess={onSuccess} />
          ))}
        </div>
      )}
    </div>
  )
}

function LoanRow({ loan, formatMoney, onSuccess }) {
  const [repayAmount, setRepayAmount] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const outstanding =
    loan.status === 'APPROVED' ? (loan.totalRepayable - loan.amountRepaid).toFixed(2) : null

  async function handleRepay(e) {
    e.preventDefault()
    setError('')
    const parsed = parseFloat(repayAmount)
    if (!parsed || parsed <= 0) {
      setError('Enter a valid repayment amount')
      return
    }

    setSubmitting(true)
    try {
      await apiClient.post(`/loans/${loan.id}/repay`, { amount: parsed })
      setRepayAmount('')
      onSuccess()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ borderBottom: '1px solid var(--border-hairline)', paddingBottom: '1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div className="mono" style={{ fontWeight: 600 }}>{formatMoney(loan.amount)}</div>
          <div style={{ fontSize: '0.85rem', color: 'var(--ink-muted)' }}>{loan.reason}</div>
        </div>
        <span className={`stamp ${STAMP_CLASSES[loan.status]}`}>{loan.status}</span>
      </div>

      {loan.status === 'APPROVED' && (
        <div style={{ marginTop: '0.75rem' }}>
          <div style={{ fontSize: '0.85rem', marginBottom: '0.5rem' }}>
            Outstanding: <strong className="mono">{formatMoney(outstanding)}</strong>
          </div>

          {error && <div className="form-error-banner">{error}</div>}

          <form onSubmit={handleRepay} style={{ display: 'flex', gap: '0.5rem' }}>
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={repayAmount}
              onChange={(e) => setRepayAmount(e.target.value)}
              placeholder="Repayment amount"
              style={{
                flex: 1,
                padding: '0.5rem 0.7rem',
                border: '1.5px solid var(--border-hairline)',
                borderRadius: 'var(--radius-sm)',
              }}
            />
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Paying…' : 'Repay'}
            </button>
          </form>
        </div>
      )}
    </div>
  )
}