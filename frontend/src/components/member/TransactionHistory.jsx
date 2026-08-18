/**
 * Renders the account's transactions as a numbered ledger table.
 * "Entry No." counts down from the most recent - a real sequence,
 * not decorative numbering, since these genuinely are chronological.
 */
export default function TransactionHistory({ transactions }) {
  const formatMoney = (value) =>
    new Intl.NumberFormat('en-KE', { style: 'currency', currency: 'KES' }).format(value)

  const formatDate = (isoString) =>
    new Date(isoString).toLocaleDateString('en-KE', { day: 'numeric', month: 'short', year: 'numeric' })

  const isCredit = (type) => type === 'DEPOSIT' || type === 'LOAN_DISBURSEMENT'

  return (
    <div className="card">
      <div className="card-header">
        <h2>Transaction history</h2>
      </div>

      {transactions.length === 0 ? (
        <div className="empty-state">No transactions yet. Make your first deposit above.</div>
      ) : (
        <table className="ledger-table">
          <thead>
            <tr>
              <th>Entry No.</th>
              <th>Date</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Balance after</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((txn, index) => (
              <tr key={txn.id}>
                <td className="entry-no">{String(transactions.length - index).padStart(4, '0')}</td>
                <td>{formatDate(txn.timestamp)}</td>
                <td>{txn.type.replace('_', ' ')}</td>
                <td className={isCredit(txn.type) ? 'amount-positive' : 'amount-negative'}>
                  {isCredit(txn.type) ? '+' : '−'}
                  {formatMoney(txn.amount)}
                </td>
                <td>{formatMoney(txn.balanceAfter)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}