package com.byteentropy.auth_core.service;

import com.byteentropy.auth_core.config.JwtUtils;
import com.byteentropy.auth_core.domain.Identity;
import com.byteentropy.auth_core.domain.IdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.ObjectProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Use Lombok for logging
public class AuthService {
    private final IdentityRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    
    // ObjectProvider allows the app to start even if Kafka bean isn't fully configured
    private final ObjectProvider<KafkaTemplate<String, String>> kafkaProvider;

    public String authenticate(String identifier, String rawSecret) {
        Optional<Identity> identityOpt = repository.findByIdentifier(identifier);

        if (identityOpt.isPresent() && encoder.matches(rawSecret, identityOpt.get().getSecret())) {
            sendEvent("SUCCESS:" + identifier);
            return jwtUtils.createToken(identityOpt.get().getIdentifier(), identityOpt.get().getPermissions());
        }

        sendEvent("FAILURE:" + identifier);
        log.warn("Failed login attempt for identifier: {}", identifier);
        throw new RuntimeException("Authentication Failed");
    }

    private void sendEvent(String message) {
        try {
            kafkaProvider.ifAvailable(k -> k.send("auth-events", message));
        } catch (Exception e) {
            log.error("Failed to send Kafka event: {}", e.getMessage());
            // We don't throw here so the login still works even if Kafka is down
        }
    }
}