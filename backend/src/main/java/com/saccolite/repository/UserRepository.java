package com.saccolite.repository;

import com.saccolite.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data access for User.
 * JpaRepository already gives us save(), findById(), findAll(), delete(), etc.
 * We only need to declare the extra lookups specific to our use case.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Used at login: find a user by their email to check password + issue a JWT.
    Optional<User> findByEmail(String email);

    // Used at registration: make sure the email isn't already taken.
    boolean existsByEmail(String email);
}