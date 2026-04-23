package com.byteentropy.auth_core.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IdentityRepository extends JpaRepository<Identity, String> {
    Optional<Identity> findByIdentifier(String identifier);
}