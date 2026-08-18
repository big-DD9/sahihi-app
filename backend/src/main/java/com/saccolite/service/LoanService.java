package com.saccolite.service;

import com.saccolite.exception.ResourceNotFoundException;
import com.saccolite.model.*;
import com.saccolite.repository.LoanRepository;
import com.saccolite.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Core business logic for loans: requesting, approving/rejecting, and repaying.
 *
 * Eligibility rule: a member can request a loan up to 3x their current
 * savings balance. This mirrors how real SACCOs typically size loans
 * against member deposits/shares.
 */
@Service
@RequiredArgsConstructor
public class LoanService {

    private static final BigDecimal ELIGIBILITY_MULTIPLIER = BigDecimal.valueOf(3);

    private final LoanRepository loanRepository;
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Loan requestLoan(User user, BigDecimal amount, String reason) {
        Account account = accountService.getAccountForUser(user);

        BigDecimal maxEligible = account.getBalance().multiply(ELIGIBILITY_MULTIPLIER);
        if (amount.compareTo(maxEligible) > 0) {
            throw new IllegalArgumentException(
                    "Requested amount exceeds your loan limit of " + maxEligible +
                    " (3x your current savings balance of " + account.getBalance() + ")"
            );
        }

        Loan loan = Loan.builder()
                .account(account)
                .amount(amount)
                .reason(reason)
                .status(LoanStatus.PENDING)
                .build();

        return loanRepository.save(loan);
    }

    public List<Loan> getLoansForUser(User user) {
        Account account = accountService.getAccountForUser(user);
        return loanRepository.findByAccountIdOrderByRequestedAtDesc(account.getId());
    }

    public List<Loan> getPendingLoans() {
        return loanRepository.findByStatusOrderByRequestedAtAsc(LoanStatus.PENDING);
    }

    /**
     * Admin decision on a pending loan. Approving disburses funds straight
     * into the member's account and logs it as a transaction, same as a
     * deposit would be - so the audit trail stays consistent everywhere.
     */
    @Transactional
    public Loan decideLoan(Long loanId, boolean approve, BigDecimal interestRate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalArgumentException("This loan has already been decided");
        }

        loan.setDecidedAt(LocalDateTime.now());

        if (!approve) {
            loan.setStatus(LoanStatus.REJECTED);
            return loanRepository.save(loan);
        }

        if (interestRate == null) {
            throw new IllegalArgumentException("Interest rate is required to approve a loan");
        }

        // totalRepayable = principal + (principal * rate / 100)
        BigDecimal interestAmount = loan.getAmount()
                .multiply(interestRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = loan.getAmount().add(interestAmount);

        loan.setStatus(LoanStatus.APPROVED);
        loan.setInterestRate(interestRate);
        loan.setTotalRepayable(totalRepayable);
        loan = loanRepository.save(loan);

        // Disburse: credit the member's account and log it, mirroring how
        // AccountService logs deposits - keeps every balance change auditable.
        Account account = loan.getAccount();
        BigDecimal newBalance = account.getBalance().add(loan.getAmount());
        account.setBalance(newBalance);

        Transaction disbursement = Transaction.builder()
                .account(account)
                .type(TransactionType.LOAN_DISBURSEMENT)
                .amount(loan.getAmount())
                .balanceAfter(newBalance)
                .description("Loan disbursement for loan #" + loan.getId())
                .build();
        transactionRepository.save(disbursement);

        return loan;
    }

    @Transactional
    public Loan repayLoan(User user, Long loanId, BigDecimal amount) {
        Account account = accountService.getAccountForUser(user);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (!loan.getAccount().getId().equals(account.getId())) {
            throw new IllegalArgumentException("This loan does not belong to your account");
        }
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved, outstanding loans can be repaid");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance to make this repayment");
        }

        BigDecimal outstanding = loan.getTotalRepayable().subtract(loan.getAmountRepaid());
        if (amount.compareTo(outstanding) > 0) {
            throw new IllegalArgumentException("Repayment exceeds outstanding balance of " + outstanding);
        }

        // Debit the member's savings account for the repayment amount.
        BigDecimal newAccountBalance = account.getBalance().subtract(amount);
        account.setBalance(newAccountBalance);

        Transaction repaymentTxn = Transaction.builder()
                .account(account)
                .type(TransactionType.LOAN_REPAYMENT)
                .amount(amount)
                .balanceAfter(newAccountBalance)
                .description("Repayment for loan #" + loan.getId())
                .build();
        transactionRepository.save(repaymentTxn);

        // Update the loan's own repayment progress.
        BigDecimal newAmountRepaid = loan.getAmountRepaid().add(amount);
        loan.setAmountRepaid(newAmountRepaid);
        if (newAmountRepaid.compareTo(loan.getTotalRepayable()) >= 0) {
            loan.setStatus(LoanStatus.REPAID);
        }

        return loanRepository.save(loan);
    }
}