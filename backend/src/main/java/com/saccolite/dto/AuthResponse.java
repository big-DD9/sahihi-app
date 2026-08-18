package com.saccolite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What we send back after a successful /api/auth/login or /api/auth/register.
 * The frontend stores the token and attaches it to every future request
 * as "Authorization: Bearer <token>".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;
}