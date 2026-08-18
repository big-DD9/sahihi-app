package com.saccolite.service;

import com.saccolite.dto.AuthResponse;
import com.saccolite.dto.LoginRequest;
import com.saccolite.dto.RegisterRequest;
import com.saccolite.model.Account;
import com.saccolite.model.Role;
import com.saccolite.model.User;
import com.saccolite.repository.AccountRepository;
import com.saccolite.repository.UserRepository;
import com.saccolite.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Handles the two entry points into the system: register and login.
 *
 * Registration also auto-creates a savings Account for the new member -
 * in Sahihi, every member always has exactly one account, so there's
 * no separate "open an account" step for the user to worry about.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // never store plain text
                .role(Role.MEMBER) // public registration always creates a MEMBER, never ADMIN
                .build();

        user = userRepository.save(user);

        // Every new member automatically gets a savings account with a zero balance.
        Account account = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber(user.getId()))
                .balance(BigDecimal.ZERO)
                .build();
        accountRepository.save(account);

        String token = jwtService.generateToken(toUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // This throws automatically if credentials are wrong - Spring Security
        // checks the password against the BCrypt hash for us.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(toUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    private String generateAccountNumber(Long userId) {
        // Simple readable format: SAH-00001, SAH-00002, etc.
        return String.format("SAH-%05d", userId);
    }
}