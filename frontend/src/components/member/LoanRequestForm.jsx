import { useState } from 'react'
import apiClient from '../../api/client'

/**
 * Member-facing loan request form. Eligibility (amount <= 3x balance)
 * is enforced server-side in LoanService - this form just collects
 * input and surfaces whatever error the backend returns.
 */
export default function LoanRequestForm({ onSuccess }) {
  const [amount, setAmount] = useState('')
  const [reason, setReason] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    const parsedAmount = parseFloat(amount)
    if (!parsedAmount || parsedAmount <= 0) {
      setError('Enter an amount greater than zero')
      return
    }

    setSubmitting(true)
    try {
      await apiClient.post('/loans', { amount: parsedAmount, reason })
      setAmount('')
      setReason('')
      onSuccess()
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="card">
      <div className="card-header">
        <h2>Request a loan</h2>
      </div>

      <p style={{ color: 'var(--ink-muted)', fontSize: '0.85rem', marginTop: 0, marginBottom: '1rem' }}>
        You can request up to 3× your current savings balance.
      </p>

      {error && <div className="form-error-banner">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="loanAmount">Amount (KES)</label>
          <input
            id="loanAmount"
            type="number"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0.00"
            required
          />
        </div>

        <div className="field">
          <label htmlFor="reason">Reason</label>
          <textarea
            id="reason"
            rows={3}
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            placeholder="What's the loan for?"
            required
          />
        </div>

        <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
          {submitting ? 'Submitting…' : 'Submit request'}
        </button>
      </form>
    </div>
  )
}