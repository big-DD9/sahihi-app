package com.saccolite.model;

/**
 * Lifecycle states of a loan.
 * PENDING   - member has requested it, awaiting admin decision
 * APPROVED  - admin approved it, funds disbursed to the account
 * REJECTED  - admin declined the request
 * REPAID    - full outstanding amount has been paid back
 */
public enum LoanStatus {
    PENDING,
    APPROVED,
    REJECTED,
    REPAID
}