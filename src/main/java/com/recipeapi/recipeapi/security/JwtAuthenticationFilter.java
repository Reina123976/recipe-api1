package com.recipeapi.recipeapi.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for JWT-based authentication.
 *
 * <p>This filter intercepts incoming requests, extracts and validates JWT tokens,
 * and sets up the Spring Security context if the token is valid.</p>
 *
 * @author Reina
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Use SLF4J Logger instead of commons logging
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService; // Changed from JwtUtil to JwtService

    /**
     * Creates a new JwtAuthenticationFilter with the necessary dependencies.
     *
     * @param userDetailsService Service for loading user details
     * @param jwtService Service for JWT operations
     */
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService; // Changed from jwtUtil to jwtService
    }

    /**
     * Filters incoming requests to extract and validate JWT tokens.
     *
     * <p>This method:
     * <ul>
     *   <li>Extracts the JWT token from the Authorization header</li>
     *   <li>Validates the token and extracts the username</li>
     *   <li>Loads the user details and sets up the security context if the token is valid</li>
     * </ul>
     * </p>
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain
     * @throws ServletException If a servlet exception occurs
     * @throws IOException If an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtService.extractUsername(jwt); // Changed from jwtUtil to jwtService
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) { // Changed from jwtUtil to jwtService
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            // Use proper string formatting for SLF4J
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}