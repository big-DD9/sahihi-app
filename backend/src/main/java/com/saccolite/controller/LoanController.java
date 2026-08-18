package com.saccolite.controller;

import com.saccolite.dto.LoanDecisionRequest;
import com.saccolite.dto.LoanRequest;
import com.saccolite.exception.ResourceNotFoundException;
import com.saccolite.model.Loan;
import com.saccolite.model.User;
import com.saccolite.repository.UserRepository;
import com.saccolite.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    // MEMBER: request a new loan
    @PostMapping
    public ResponseEntity<Loan> requestLoan(Authentication authentication, @Valid @RequestBody LoanRequest request) {
        User user = currentUser(authentication);
        Loan loan = loanService.requestLoan(user, request.getAmount(), request.getReason());
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    // MEMBER: view their own loan history
    @GetMapping("/me")
    public ResponseEntity<List<Loan>> getMyLoans(Authentication authentication) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(loanService.getLoansForUser(user));
    }

    // MEMBER: repay an approved loan
    @PostMapping("/{loanId}/repay")
    public ResponseEntity<Loan> repayLoan(
            Authentication authentication,
            @PathVariable Long loanId,
            @RequestBody LoanRepaymentAmount body
    ) {
        User user = currentUser(authentication);
        Loan loan = loanService.repayLoan(user, loanId, body.amount());
        return ResponseEntity.ok(loan);
    }

    // ADMIN only: view all loans awaiting a decision.
    // @PreAuthorize checks the "ROLE_ADMIN" authority we set back in
    // UserDetailsServiceImpl - a MEMBER hitting this gets a 403, not a 200.
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Loan>> getPendingLoans() {
        return ResponseEntity.ok(loanService.getPendingLoans());
    }

    // ADMIN only: approve or reject a pending loan
    @PutMapping("/{loanId}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Loan> decideLoan(@PathVariable Long loanId, @Valid @RequestBody LoanDecisionRequest request) {
        Loan loan = loanService.decideLoan(loanId, request.getApprove(), request.getInterestRate());
        return ResponseEntity.ok(loan);
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Tiny inline record just for the repay endpoint's request body -
    // not worth a full DTO file for a single BigDecimal field.
    public record LoanRepaymentAmount(BigDecimal amount) {}
}