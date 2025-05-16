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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

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