package com.byteentropy.auth_core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = AuthCoreApplication.class)
@ActiveProfiles("test") // Good practice to use a 'test' profile
@EmbeddedKafka(partitions = 1)
class AuthCoreApplicationTests {

    @Test
    void contextLoads() {
        // This test will fail if any Bean (like JwtUtils or SecurityConfig) is misconfigured
    }
}
