package com.byteentropy.auth_core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * The core Identity entity for the Abc Auth-Core system.
 * Supports the Builder pattern for clean object creation in tests and services.
 */
@Entity
@Table(name = "identities")
@Getter 
@Setter 
@NoArgsConstructor  // Required by JPA/Hibernate
@AllArgsConstructor // Required by @Builder
@Builder            // Enables Identity.builder()
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String identifier; // email for humans or key-id for services

    @Column(nullable = false)
    private String secret;     // BCrypt hashed password or API secret

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "identity_permissions", 
        joinColumns = @JoinColumn(name = "identity_id")
    )
    @Column(name = "permission")
    private Set<String> permissions; // Fine-grained permission strings
}
