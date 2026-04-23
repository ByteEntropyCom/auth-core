package com.byteentropy.auth_core.dto;

/**
 * Immutable response record.
 * Standardizes the 'Bearer' type out of the box.
 */

public record AuthResponse(String token, String type) {
    
    // Canonical constructor for custom logic
    public AuthResponse(String token) {
        this(token, "Bearer");
    }
}