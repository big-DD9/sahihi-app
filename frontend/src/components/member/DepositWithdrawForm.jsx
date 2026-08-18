import { useState } from 'react'
import apiClient from '../../api/client'

/**
 * Handles both deposit and withdraw - same amount field, different
 * endpoint depending on which button was clicked. onSuccess refetches
 * the account/history in the parent so the balance updates immediately.
 */
export default function DepositWithdrawForm({ onSuccess }) {
  const [amount, setAmount] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleAction(type) {
    setError('')
    const parsedAmount = parseFloat(amount)

    if (!parsedAmount || parsedAmount <= 0) {
      setError('Enter an amount greater than zero')
      return
    }

    setSubmitting(true)
    try {
      await apiClient.post(`/accounts/${type}`, { amount: parsedAmount })
      setAmount('')
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
        <h2>Move money</h2>
      </div>

      {error && <div className="form-error-banner">{error}</div>}

      <div className="field">
        <label htmlFor="amount">Amount (KES)</label>
        <input
          id="amount"
          type="number"
          min="0.01"
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="0.00"
        />
      </div>

      <div className="btn-group">
        <button
          className="btn btn-primary"
          onClick={() => handleAction('deposit')}
          disabled={submitting}
        >
          Deposit
        </button>
        <button
          className="btn btn-secondary"
          onClick={() => handleAction('withdraw')}
          disabled={submitting}
        >
          Withdraw
        </button>
      </div>
    </div>
  )
}