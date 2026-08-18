package com.saccolite.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a loan requested by a member against their savings account.
 * Business rule enforced in LoanService: requested amount cannot exceed
 * 3x the member's current savings balance at time of request.
 */
@Entity
@Table(name = "loans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String reason;

    // Interest rate as a percentage, e.g. 10.00 = 10%. Set by admin on approval.
    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    // amount + interest. Calculated on approval, this is what must be repaid in full.
    @Column(precision = 19, scale = 2)
    private BigDecimal totalRepayable;

    // Running total of repayments made so far.
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountRepaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime decidedAt; // when admin approved/rejected

    @PrePersist
    protected void onCreate() {
        this.requestedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = LoanStatus.PENDING;
        }
        if (this.amountRepaid == null) {
            this.amountRepaid = BigDecimal.ZERO;
        }
    }
}