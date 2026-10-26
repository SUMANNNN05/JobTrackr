package com.jobtrackr;

import com.jobtrackr.security.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("unit-test-secret-key-that-is-long-enough-123".getBytes());

    @Test
    void roundTripsSubject() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        assertEquals("a@b.com", jwt.extractSubject(jwt.generateToken("a@b.com")));
    }

    @Test
    void rejectsTamperedToken() {
        JwtService jwt = new JwtService(SECRET, 60_000);
        String token = jwt.generateToken("a@b.com") + "x";
        assertThrows(JwtException.class, () -> jwt.extractSubject(token));
    }

    @Test
    void rejectsExpiredToken() {
        JwtService jwt = new JwtService(SECRET, -1000);
        String token = jwt.generateToken("a@b.com");
        assertThrows(JwtException.class, () -> jwt.extractSubject(token));
    }
}
