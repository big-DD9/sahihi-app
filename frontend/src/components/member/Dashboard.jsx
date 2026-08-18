/**
 * Balance hero card - the "ledger page header". Shows account number
 * and current balance in the big Fraunces display type.
 */
export default function Dashboard({ account }) {
  if (!account) return null

  const formattedBalance = new Intl.NumberFormat('en-KE', {
    style: 'currency',
    currency: 'KES',
  }).format(account.balance)

  return (
    <div className="card">
      <div className="balance-hero">
        <div>
          <div className="account-number">Account {account.accountNumber}</div>
          <div className="balance-amount">{formattedBalance}</div>
        </div>
      </div>
    </div>
  )
}