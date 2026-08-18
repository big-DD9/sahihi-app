package com.saccolite.repository;

import com.saccolite.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data access for Transaction (the ledger/audit trail).
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Powers the "transaction history" view on the dashboard.
    // Ordered newest-first so the most recent activity shows at the top.
    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
}