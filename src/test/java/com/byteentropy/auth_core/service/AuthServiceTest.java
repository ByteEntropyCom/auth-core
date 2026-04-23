package com.byteentropy.auth_core.service;

import com.byteentropy.auth_core.config.JwtUtils;
import com.byteentropy.auth_core.domain.Identity;
import com.byteentropy.auth_core.domain.IdentityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @Mock private IdentityRepository repository;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private ObjectProvider<KafkaTemplate<String, String>> kafkaProvider;

    @InjectMocks
    private AuthService authService;

    private Identity testIdentity;

    @BeforeEach
    void setUp() {
        testIdentity = Identity.builder()
                .identifier("test@test.com")
                .secret("hashed_password")
                .permissions(Set.of("ROLE_USER"))
                .build();
    }

    @Test
    void authenticate_Success() {
        // Arrange
        when(repository.findByIdentifier("test@test.com")).thenReturn(Optional.of(testIdentity));
        when(encoder.matches("raw_password", "hashed_password")).thenReturn(true);
        when(jwtUtils.createToken(anyString(), anySet())).thenReturn("mock_token");

        // Act
        String token = authService.authenticate("test@test.com", "raw_password");

        // Assert
        assertEquals("mock_token", token);
        verify(repository).findByIdentifier("test@test.com");
    }

    @Test
    void authenticate_Failure_WrongPassword() {
        // Arrange
        when(repository.findByIdentifier("test@test.com")).thenReturn(Optional.of(testIdentity));
        when(encoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authService.authenticate("test@test.com", "wrong_password")
        );
    }
}