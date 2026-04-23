package com.byteentropy.auth_core;

import com.byteentropy.auth_core.domain.Identity;
import com.byteentropy.auth_core.domain.IdentityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestPropertySource(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private IdentityRepository repository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        Identity user = Identity.builder()
                .identifier("integration@test.com")
                .secret(passwordEncoder.encode("secret123"))
                .permissions(Set.of("ROLE_USER"))
                .build();
        repository.save(user);
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        var loginRequest = Map.of(
            "identifier", "integration@test.com",
            "secret", "secret123"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void login_ShouldReturn401_WhenCredentialsAreInvalid() throws Exception {
        var loginRequest = Map.of(
            "identifier", "integration@test.com",
            "secret", "wrong_password"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Failed"));
    }
}