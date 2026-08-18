package com.saccolite.service;

import com.saccolite.exception.ResourceNotFoundException;
import com.saccolite.model.Account;
import com.saccolite.model.Transaction;
import com.saccolite.model.TransactionType;
import com.saccolite.model.User;
import com.saccolite.repository.AccountRepository;
import com.saccolite.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Handles everything related to a member's savings account: deposits,
 * withdrawals, and reading transaction history.
 *
 * Every balance change goes through here so we can guarantee a
 * Transaction row is always written alongside it - no code path should
 * ever update Account.balance directly without logging why.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Account getAccountForUser(User user) {
        return accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No savings account found for this user"));
    }

    /**
     * @Transactional matters here: if the Transaction save fails after the
     * Account save succeeds (or vice versa), we don't want a balance change
     * with no audit trail, or a logged transaction that doesn't match the
     * real balance. Both writes succeed together or both roll back.
     */
    @Transactional
    public Account deposit(User user, BigDecimal amount) {
        Account account = getAccountForUser(user);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        logTransaction(account, TransactionType.DEPOSIT, amount, newBalance, "Member deposit");
        return account;
    }

    @Transactional
    public Account withdraw(User user, BigDecimal amount) {
        Account account = getAccountForUser(user);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance for this withdrawal");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        logTransaction(account, TransactionType.WITHDRAWAL, amount, newBalance, "Member withdrawal");
        return account;
    }

    public List<Transaction> getTransactionHistory(User user) {
        Account account = getAccountForUser(user);
        return transactionRepository.findByAccountIdOrderByTimestampDesc(account.getId());
    }

    private void logTransaction(Account account, TransactionType type, BigDecimal amount,
                                 BigDecimal balanceAfter, String description) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .build();
        transactionRepository.save(transaction);
    }
}