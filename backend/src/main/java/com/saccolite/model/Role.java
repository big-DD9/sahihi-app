package com.saccolite.model;

/**
 * Defines the two user roles in Sahihi.
 * MEMBER   - regular user: can manage own savings account, request loans, make repayments.
 * ADMIN    - staff user: can review and approve/reject loan requests across all members.
 */
public enum Role {
    MEMBER,
    ADMIN
}