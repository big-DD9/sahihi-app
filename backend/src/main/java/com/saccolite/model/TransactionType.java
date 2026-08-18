package com.saccolite.model;

/**
 * Type of movement recorded against a savings Account.
 * DEPOSIT       - money added by the member
 * WITHDRAWAL    - money taken out by the member
 * LOAN_DISBURSEMENT - loan amount credited to the account on approval
 * LOAN_REPAYMENT     - repayment amount debited from the account
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    LOAN_DISBURSEMENT,
    LOAN_REPAYMENT
}