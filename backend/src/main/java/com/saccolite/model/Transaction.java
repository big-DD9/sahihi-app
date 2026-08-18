package com.saccolite.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A single ledger entry against an Account: every deposit, withdrawal,
 * loan disbursement, or loan repayment creates one of these.
 * This is what makes the account's balance auditable rather than
 * just a raw number that gets overwritten with no history.
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // Balance snapshot AFTER this transaction was applied.
    // Storing this makes it trivial to show history without recalculating.
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    private String description; // e.g. "Loan disbursement for LOAN-00004"

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}