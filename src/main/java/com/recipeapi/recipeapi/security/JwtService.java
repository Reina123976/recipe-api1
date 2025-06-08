package com.recipeapi.recipeapi.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for JWT operations.
 *
 * <p>This service provides methods for generating, validating, and parsing JWT tokens.</p>
 *
 * @author Reina
 * @version 1.0
 */
@Component
public class JwtService {

    @Value("${jwt.secret:defaultsecretkey}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    // Remove the static key initialization
    // private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Add method to get consistent signing key
    /**
     * Gets the signing key for JWT operations.
     *
     * @return A SecretKey for signing JWTs
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token
     * @return The username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token
     * @return The expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * Extracts a claim from a JWT token.
     *
     * @param token The JWT token
     * @param claimsResolver Function to extract the desired claim
     * @param <T> The type of the claim
     * @return The extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token
     * @return All claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Use getSigningKey() method
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * Checks if a JWT token is expired.
     *
     * @param token The JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    /**
     * Generates a JWT token for a username.
     *
     * @param username The username to include in the token
     * @return The generated JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Add overloaded method to accept UserDetails for compatibility
    /**
     * Generates a JWT token for a user.
     *
     * @param userDetails The user details
     * @return The generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }


    /**
     * Creates a JWT token with the specified claims and subject.
     *
     * @param claims Additional claims to include in the token
     * @param subject The subject of the token (typically the username)
     * @return The generated JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey()) // Use getSigningKey() method
                .compact();
    }

    /**
     * Validates a JWT token for a user.
     *
     * @param token The JWT token to validate
     * @param userDetails The user details to validate against
     * @return true if the token is valid for the user, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}