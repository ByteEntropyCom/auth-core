package com.byteentropy.auth_core.web;

import com.byteentropy.auth_core.dto.AuthRequest;
import com.byteentropy.auth_core.dto.AuthResponse;
import com.byteentropy.auth_core.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        
        String token = authService.authenticate(
            request.identifier(), 
            request.secret()
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }
}