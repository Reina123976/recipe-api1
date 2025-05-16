package com.recipeapi.recipeapi.security;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String username = "testuser";

    @BeforeEach
    public void setup() {
        userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(new ArrayList<>())
                .build();
    }

    @Test
    public void testGenerateTokenFromUsername() {
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGenerateTokenFromUserDetails() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testExtractUsername() {
        String token = jwtService.generateToken(username);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    public void testExtractExpiration() {
        String token = jwtService.generateToken(username);
        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    public void testExtractClaim() {
        String token = jwtService.generateToken(username);

        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals(username, subject);

        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()) || issuedAt.equals(new Date()));
    }

    @Test
    public void testValidateToken() {
        String token = jwtService.generateToken(username);

        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    public void testValidateTokenWithWrongUser() {
        String token = jwtService.generateToken(username);
        UserDetails wrongUser = User.builder()
                .username("wronguser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        boolean isValid = jwtService.validateToken(token, wrongUser);
        assertFalse(isValid);
    }
}