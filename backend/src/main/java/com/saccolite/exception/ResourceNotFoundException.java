package com.saccolite.exception;

/**
 * Thrown whenever a lookup fails - e.g. account or loan not found by id.
 * Caught by GlobalExceptionHandler and turned into a clean 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}