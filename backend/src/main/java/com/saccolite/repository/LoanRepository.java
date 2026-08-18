package com.saccolite.repository;

import com.saccolite.model.Loan;
import com.saccolite.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data access for Loan.
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // A member viewing their own loan history.
    List<Loan> findByAccountIdOrderByRequestedAtDesc(Long accountId);

    // Powers the admin panel: "show me all loans awaiting a decision".
    List<Loan> findByStatusOrderByRequestedAtAsc(LoanStatus status);
}