package com.saccolite.controller;

import com.saccolite.dto.DepositRequest;
import com.saccolite.exception.ResourceNotFoundException;
import com.saccolite.model.Account;
import com.saccolite.model.Transaction;
import com.saccolite.model.User;
import com.saccolite.repository.UserRepository;
import com.saccolite.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * All endpoints here require a valid JWT (SecurityConfig: anyRequest().authenticated()).
 * The logged-in user is resolved from the Authentication object, which
 * JwtAuthFilter populated earlier in the request lifecycle.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Account> getMyAccount(Authentication authentication) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(accountService.getAccountForUser(user));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Account> deposit(Authentication authentication, @Valid @RequestBody DepositRequest request) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(accountService.deposit(user, request.getAmount()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Account> withdraw(Authentication authentication, @Valid @RequestBody DepositRequest request) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(accountService.withdraw(user, request.getAmount()));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(Authentication authentication) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(accountService.getTransactionHistory(user));
    }

    // Authentication.getName() returns the email (that's what we set as the
    // JWT "subject" and Spring Security "username" throughout this app).
    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}