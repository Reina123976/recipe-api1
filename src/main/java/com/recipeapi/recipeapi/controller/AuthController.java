package com.recipeapi.recipeapi.controller;
import com.recipeapi.recipeapi.model.User;
import com.recipeapi.recipeapi.security.JwtService;
import com.recipeapi.recipeapi.security.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for handling authentication operations.
 *
 * <p>This controller provides endpoints for user login and registration.</p>
 *
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * Creates a new AuthController with the necessary dependencies.
     *
     * @param authenticationManager Manager for authenticating users
     * @param userService Service for user operations
     * @param jwtService Service for JWT operations
     */
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * <p>This endpoint accepts a username and password, validates them,
     * and returns a JWT token if authentication is successful.</p>
     *
     * @param loginRequest A map containing "username" and "password" keys
     * @return ResponseEntity containing the JWT token and username, or an error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.get("username"),
                            loginRequest.get("password")
                    )
            );

            // Generate JWT token
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            final String jwt = jwtService.generateToken(userDetails.getUsername());

            // Return the token
            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    /**
     * Registers a new user.
     *
     * <p>This endpoint accepts user registration data, validates it,
     * and creates a new user account if validation passes.</p>
     *
     * @param registerRequest A map containing user registration data
     * @return ResponseEntity with confirmation message or error details
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Map<String, Object> registerRequest) {
        try {
            String username = (String) registerRequest.get("username");
            String password = (String) registerRequest.get("password");
            String email = (String) registerRequest.get("email");

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) registerRequest.get("roles");

            // Register the user
            User user = userService.registerUser(username, password, email, roles);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}