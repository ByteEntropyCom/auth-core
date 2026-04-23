package com.byteentropy.auth_core.config;

import com.byteentropy.auth_core.domain.Identity;
import com.byteentropy.auth_core.domain.IdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Seeds initial data into the H2 database on startup.
 * This ensures you have a user to test the /auth/login endpoint immediately.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IdentityRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        String testEmail = "admin@byteentropy.com";

        // Check if the identity already exists to avoid duplicate entries on restarts
        if (repository.findByIdentifier(testEmail).isEmpty()) {
            
            Identity admin = Identity.builder()
                    .identifier(testEmail)
                    // We must encode the password because the AuthService matches against the hash
                    .secret(passwordEncoder.encode("password123")) 
                    .permissions(Set.of("READ_PRIVILEGE", "WRITE_PRIVILEGE"))
                    .build();

            repository.save(admin);

            System.out.println("=================================================");
            System.out.println("🚀 AUTH-CORE: Test Identity Created!");
            System.out.println("Identifier: " + testEmail);
            System.out.println("Password:   password123");
            System.out.println("=================================================");
        } else {
            System.out.println("ℹ️ AUTH-CORE: Test Identity already exists. Skipping initialization.");
        }
    }
}