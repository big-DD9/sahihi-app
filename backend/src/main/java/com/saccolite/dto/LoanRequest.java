package com.saccolite.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * What a member sends to POST /api/loans - requesting a new loan.
 * Eligibility (amount <= 3x savings balance) is checked in LoanService,
 * not here - validation here is just about well-formed input.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Please provide a reason for the loan")
    private String reason;
}