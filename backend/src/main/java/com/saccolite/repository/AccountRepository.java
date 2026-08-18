package com.saccolite.repository;

import com.saccolite.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data access for Account.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Used constantly: every authenticated request needs "get the current user's account".
    Optional<Account> findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);
}